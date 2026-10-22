package com.WAR.BankAPI.services;

import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.AccountType;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.AccountDAO;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = {
        "spring.datasource.url="
            + "jdbc:postgresql://localhost:5432/reactive_banking_test"
    }
)
@ActiveProfiles("test")
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transaction;
    private UUID userId;
    private Long checkingNumber;
    private Long savingsNumber;

    /*
    @BeforeEach
    void setUp() {
        // Confirm the connection before creating any test records.
        assertEquals(
            "reactive_banking_test",
            jdbcTemplate.queryForObject(
                "SELECT current_database()",
                String.class
            )
        );

        transaction = new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(status -> {
            String uniqueName = "account-test-" + UUID.randomUUID();

            User user = new User(
                "Account",
                "Test",
                uniqueName + "@example.com",
                "5550000000",
                uniqueName,
                "test-only-placeholder"
            );

            entityManager.persist(user);
            entityManager.flush();
            userId = user.getUserId();

            List<Account> accounts =
                accountService.createDefaultAccounts(user);

            checkingNumber = accounts.stream()
                .filter(account ->
                    account.getAccountType() == AccountType.CHECKING)
                .findFirst()
                .orElseThrow()
                .getAccountNumber();

            savingsNumber = accounts.stream()
                .filter(account ->
                    account.getAccountType() == AccountType.SAVINGS)
                .findFirst()
                .orElseThrow()
                .getAccountNumber();
        });
    }

    @AfterEach
    void cleanUp() {
        if (transaction == null || userId == null) {
            return;
        }

        // Remove only this test's accounts and user.
        transaction.executeWithoutResult(status -> {
            entityManager.createQuery(
                "DELETE FROM Account a WHERE a.user.userId = :userId"
            )
                .setParameter("userId", userId)
                .executeUpdate();

            User user = entityManager.find(User.class, userId);

            if (user != null) {
                entityManager.remove(user);
            }
        });
    }

    @Test
    void defaultAccountsAreSavedWithZeroBalances() {
        transaction.executeWithoutResult(status -> {
            User owner = entityManager.find(User.class, userId);

            List<Account> accounts =
                accountService.getAccountsByUser(owner);

            assertEquals(2, accounts.size());
            assertNotEquals(checkingNumber, savingsNumber);

            for (Account account : accounts) {
                assertEquals(userId, account.getUser().getUserId());
                assertEquals(
                    0,
                    new BigDecimal("0.00")
                        .compareTo(account.getBalance())
                );
            }
        });
    }

    @Test
    void balanceChangesRollBackWhenALaterStepFails() {
        // First commit a starting balance.
        transaction.executeWithoutResult(status ->
            accountService.updateAccountBalance(
                checkingNumber,
                new BigDecimal("100.00")
            )
        );

        assertBalance(checkingNumber, "100.00");
        assertBalance(savingsNumber, "0.00");

        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> transaction.executeWithoutResult(status -> {
                accountService.updateAccountBalance(
                    checkingNumber,
                    new BigDecimal("-25.00")
                );

                accountService.updateAccountBalance(
                    savingsNumber,
                    new BigDecimal("25.00")
                );

                // Send the changes to PostgreSQL before simulating failure.
                accountDAO.flush();

                throw new IllegalStateException(
                    "Simulated transaction-history failure"
                );
            })
        );

        assertEquals(
            "Simulated transaction-history failure",
            exception.getMessage()
        );

        // Both accounts must retain their previously committed balances.
        assertBalance(checkingNumber, "100.00");
        assertBalance(savingsNumber, "0.00");
    }

    @Test
    void balanceUpdateRequiresAnExistingTransaction() {
        // Intentionally call without TransactionTemplate.
        assertThrows(
            IllegalTransactionStateException.class,
            () -> accountService.updateAccountBalance(
                checkingNumber,
                new BigDecimal("10.00")
            )
        );

        assertBalance(checkingNumber, "0.00");
    }

    private void assertBalance(Long accountNumber, String expected) {
        BigDecimal actual = transaction.execute(status ->
            accountDAO.findById(accountNumber)
                .orElseThrow()
                .getBalance()
        );

        assertNotNull(actual);
        assertEquals(
            0,
            new BigDecimal(expected).compareTo(actual)
        );
    }
}