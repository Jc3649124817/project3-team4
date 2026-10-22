package com.WAR.BankAPI.DTOs;

import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.AccountType;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountDTOTest {

    @Test
    void convertsCheckingAccountToDTO() {
        Account account = mock(Account.class);

        when(account.getAccountNumber()).thenReturn(1L);
        when(account.getAccountType()).thenReturn(AccountType.CHECKING);
        when(account.getBalance()).thenReturn(new BigDecimal("125.50"));

        AccountDTO result = AccountDTO.from(account);

        assertEquals(Long.valueOf(1L), result.accountNumber());
        assertEquals("Checking", result.accountType());
        assertEquals(new BigDecimal("125.50"), result.balance());

        // Creating the response does not need to load the owner.
        verify(account, never()).getUser();
    }

    @Test
    void convertsSavingsAccountToDTO() {
        Account account = mock(Account.class);

        when(account.getAccountNumber()).thenReturn(2L);
        when(account.getAccountType()).thenReturn(AccountType.SAVINGS);
        when(account.getBalance()).thenReturn(new BigDecimal("500.00"));

        AccountDTO result = AccountDTO.from(account);

        assertEquals(Long.valueOf(2L), result.accountNumber());
        assertEquals("Savings", result.accountType());
        assertEquals(new BigDecimal("500.00"), result.balance());
    }

    @Test
    void preservesZeroBalance() {
        Account account = mock(Account.class);

        when(account.getAccountNumber()).thenReturn(3L);
        when(account.getAccountType()).thenReturn(AccountType.CHECKING);
        when(account.getBalance()).thenReturn(new BigDecimal("0.00"));

        AccountDTO result = AccountDTO.from(account);

        assertEquals(new BigDecimal("0.00"), result.balance());
    }

    @Test
    void wrapsMultipleAccountsInAccountListDTO() {
        AccountDTO checking = new AccountDTO(
            1L,
            "Checking",
            new BigDecimal("100.00")
        );

        AccountDTO savings = new AccountDTO(
            2L,
            "Savings",
            new BigDecimal("200.00")
        );

        AccountListDTO result =
            new AccountListDTO(List.of(checking, savings));

        assertEquals(List.of(checking, savings), result.accounts());
    }

    @Test
    void supportsAnEmptyAccountList() {
        AccountListDTO result = new AccountListDTO(List.of());

        assertNotNull(result.accounts());
        assertTrue(result.accounts().isEmpty());
    }
}