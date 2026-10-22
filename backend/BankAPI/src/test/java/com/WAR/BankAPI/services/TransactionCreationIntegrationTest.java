package com.WAR.BankAPI.services;

import com.WAR.BankAPI.DTOs.TransactionRequestDTO;
import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.*;
import com.WAR.BankAPI.repos.AccountDAO;
import com.WAR.BankAPI.repos.TransactionDAO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TransactionCreationIntegrationTest {
    @Autowired TransactionCreationService service;
    @Autowired AccountService accountService;
    @Autowired AccountDAO accountDAO;
    @Autowired EntityManager entityManager;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager transactionManager;
    @MockitoSpyBean TransactionDAO transactionDAO;

    TransactionTemplate transaction;
    UUID userId;
    String username;
    Long checking;
    Long savings;

    @BeforeEach
    void setUp() {
        String database = jdbc.queryForObject("SELECT current_database()", String.class);
        assertTrue("reactive_banking_test".equals(database)
                || database.startsWith("bankapi_transaction_test_"), "Use a dedicated test database.");
        transaction = new TransactionTemplate(transactionManager);
        username = "transaction-test-" + UUID.randomUUID();
        transaction.executeWithoutResult(status -> {
            User user = new User("Transaction", "Test", username + "@example.com", "5550000000",
                    username, "test-only-placeholder");
            entityManager.persist(user);
            entityManager.flush();
            userId = user.getUserId();
            List<Account> accounts = accountService.createDefaultAccounts(user);
            checking = accounts.stream().filter(a -> a.getAccountType() == AccountType.CHECKING)
                    .findFirst().orElseThrow().getAccountNumber();
            savings = accounts.stream().filter(a -> a.getAccountType() == AccountType.SAVINGS)
                    .findFirst().orElseThrow().getAccountNumber();
            accountService.updateAccountBalance(checking, new BigDecimal("100.00"));
        });
    }

    @AfterEach
    void cleanUp() {
        if (transaction == null || userId == null) return;
        transaction.executeWithoutResult(status -> {
            entityManager.createQuery("DELETE FROM Transaction t WHERE t.user.userId = :id")
                    .setParameter("id", userId).executeUpdate();
            entityManager.createQuery("DELETE FROM Account a WHERE a.user.userId = :id")
                    .setParameter("id", userId).executeUpdate();
            entityManager.remove(entityManager.find(User.class, userId));
        });
    }

    @Test
    void transferCommitsBalancesAndHistoryWithGeneratedIdAndTimestamp() {
        var result = service.putTransaction(new TransactionRequestDTO("Transfer", new BigDecimal("25.50"),
                checking, savings), username);
        assertNotNull(result.transactionId());
        assertNotNull(result.date());
        assertEquals("Transfer", result.type());
        assertBalance(checking, "74.50");
        assertBalance(savings, "25.50");
        transaction.executeWithoutResult(status -> {
            Transaction saved = transactionDAO.findById(result.transactionId()).orElseThrow();
            assertEquals(userId, saved.getUser().getUserId());
            assertEquals(new BigDecimal("25.50"), saved.getAmount());
            assertEquals(checking, saved.getOriginId());
            assertEquals(savings, saved.getDestinationId());
        });
        assertEquals(1, historyCount());
    }

    @Test
    void depositAndWithdrawalPersistCorrectBalances() {
        service.putTransaction(new TransactionRequestDTO("Deposit", new BigDecimal("25.00"),
                null, checking), username);
        service.putTransaction(new TransactionRequestDTO("Withdraw", new BigDecimal("20.00"),
                checking, null), username);
        assertBalance(checking, "105.00");
        assertEquals(2, historyCount());
    }

    @Test
    void failedCreditRollsBackTheEarlierDebit() {
        transaction.executeWithoutResult(status -> accountService.updateAccountBalance(savings,
                new BigDecimal("99999999999999999.99")));
        assertThrows(InvalidRequestException.class, () -> service.putTransaction(
                new TransactionRequestDTO("Transfer", new BigDecimal("25.00"), checking, savings), username));
        assertBalance(checking, "100.00");
        assertBalance(savings, "99999999999999999.99");
        assertEquals(0, historyCount());
    }

    @Test
    void historyFailureRollsBackBalanceUpdatesAlreadyFlushedToPostgres() {
        doAnswer(invocation -> {
            entityManager.flush();
            throw new org.springframework.dao.DataIntegrityViolationException("Simulated history write failure");
        }).when(transactionDAO).saveAndFlush(any(Transaction.class));
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> service.putTransaction(
                new TransactionRequestDTO("Transfer", new BigDecimal("25.00"), checking, savings), username));
        assertBalance(checking, "100.00");
        assertBalance(savings, "0.00");
        assertEquals(0, historyCount());
    }

    private void assertBalance(Long number, String expected) {
        BigDecimal balance = transaction.execute(status -> accountDAO.findById(number).orElseThrow().getBalance());
        assertEquals(new BigDecimal(expected), balance);
    }

    private long historyCount() {
        return transaction.execute(status -> entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.user.userId = :id", Long.class)
                .setParameter("id", userId).getSingleResult());
    }
}
