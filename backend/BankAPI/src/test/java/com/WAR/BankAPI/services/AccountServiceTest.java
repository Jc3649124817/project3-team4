package com.WAR.BankAPI.services;

import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.AccountType;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.AccountDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private AccountDAO accountDAO;
    private AccountService accountService;
    private User user;

    @BeforeEach
    void setUp() {
        accountDAO = mock(AccountDAO.class);
        accountService = new AccountService(accountDAO);
        user = new User();
    }

    @Test
    void createsCheckingAndSavingsWithZeroBalances() {
        when(accountDAO.saveAll(any()))
            .thenAnswer(invocation -> invocation.getArgument(0));

        List<Account> accounts =
            accountService.createDefaultAccounts(user);

        assertEquals(2, accounts.size());

        assertTrue(accounts.stream().anyMatch(
            account -> account.getAccountType() == AccountType.CHECKING
        ));

        assertTrue(accounts.stream().anyMatch(
            account -> account.getAccountType() == AccountType.SAVINGS
        ));

        for (Account account : accounts) {
            assertSame(user, account.getUser());
            assertEquals(
                0,
                BigDecimal.ZERO.compareTo(account.getBalance())
            );
        }

        verify(accountDAO).saveAll(any());
    }

    @Test
    void rejectsDuplicateDefaultAccounts() {
        when(accountDAO.existsByUserAndAccountType(
            user, AccountType.CHECKING
        )).thenReturn(true);

        assertThrows(
            IllegalStateException.class,
            () -> accountService.createDefaultAccounts(user)
        );

        verify(accountDAO, never()).saveAll(any());
    }

    @Test
    void rejectsMissingUser() {
        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.createDefaultAccounts(null)
        );

        verifyNoInteractions(accountDAO);
    }

    @Test
    void addsMoneyToAnAccount() {
        Account account = accountWithBalance("100.00");

        when(accountDAO.findById(1L))
            .thenReturn(Optional.of(account));

        accountService.updateAccountBalance(
            1L, new BigDecimal("25.50")
        );

        assertEquals(
            new BigDecimal("125.50"),
            account.getBalance()
        );

        verify(accountDAO).save(account);
    }

    @Test
    void subtractsMoneyFromAnAccount() {
        Account account = accountWithBalance("100.00");

        when(accountDAO.findById(1L))
            .thenReturn(Optional.of(account));

        accountService.updateAccountBalance(
            1L, new BigDecimal("-30.00")
        );

        assertEquals(
            new BigDecimal("70.00"),
            account.getBalance()
        );

        verify(accountDAO).save(account);
    }

    @Test
    void rejectsInsufficientFundsWithoutChangingBalance() {
        Account account = accountWithBalance("20.00");

        when(accountDAO.findById(1L))
            .thenReturn(Optional.of(account));

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                1L, new BigDecimal("-25.00")
            )
        );

        assertEquals(
            new BigDecimal("20.00"),
            account.getBalance()
        );

        verify(accountDAO, never()).save(any());
    }

    @Test
    void rejectsMissingAccount() {
        when(accountDAO.findById(999L))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                999L, new BigDecimal("10.00")
            )
        );

        verify(accountDAO, never()).save(any());
    }

    @Test
    void rejectsInvalidAccountNumbersBeforeDatabaseAccess() {
        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                null, new BigDecimal("10.00")
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                0L, new BigDecimal("10.00")
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                -1L, new BigDecimal("10.00")
            )
        );

        verifyNoInteractions(accountDAO);
    }

    @Test
    void rejectsInvalidAmountsBeforeDatabaseAccess() {
        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(1L, null)
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                1L, BigDecimal.ZERO
            )
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                1L, new BigDecimal("1.001")
            )
        );

        verifyNoInteractions(accountDAO);
    }

    @Test
    void rejectsBalanceBeyondDatabaseCapacity() {
        Account account =
            accountWithBalance("99999999999999999.99");

        when(accountDAO.findById(1L))
            .thenReturn(Optional.of(account));

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.updateAccountBalance(
                1L, new BigDecimal("0.01")
            )
        );

        assertEquals(
            new BigDecimal("99999999999999999.99"),
            account.getBalance()
        );

        verify(accountDAO, never()).save(any());
    }

    @Test
    void doesNotReturnAnAccountForADifferentOwner() {
        User anotherUser = new User();

        when(accountDAO.findByAccountNumberAndUser(1L, anotherUser))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.getAccountForUser(1L, anotherUser)
        );

        verify(accountDAO).findByAccountNumberAndUser(1L, anotherUser);
        verify(accountDAO, never()).findById(any());
    }

    private Account accountWithBalance(String balance) {
        Account account = new Account(user, AccountType.CHECKING);
        account.setBalance(new BigDecimal(balance));
        return account;
    }
}