package com.WAR.BankAPI.services;

import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.AccountType;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.AccountDAO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AccountService {

    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    // Registration must save the user before calling this method.
    @Transactional
    public List<Account> createDefaultAccounts(User user) {
        requireUser(user);

        Boolean hasChecking = accountDAO.existsByUserAndAccountType(
            user,
            AccountType.CHECKING
        );

        Boolean hasSavings = accountDAO.existsByUserAndAccountType(
            user,
            AccountType.SAVINGS
        );

        if (Boolean.TRUE.equals(hasChecking)
                || Boolean.TRUE.equals(hasSavings)) {
            throw new IllegalStateException(
                "Default accounts already exist for this user."
            );
        }

        Account checking = new Account(user, AccountType.CHECKING);
        Account savings = new Account(user, AccountType.SAVINGS);

        return accountDAO.saveAll(List.of(checking, savings));
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsByUser(User user) {
        requireUser(user);

        return accountDAO.findByUser(user);
    }

    // Internal lookup: this does not verify who owns the account.
    @Transactional(readOnly = true)
    public Account getAccountByAccountNumber(Long accountNumber) {
        requireAccountNumber(accountNumber);

        return accountDAO.findById(accountNumber)
            .orElseThrow(() ->
                new IllegalArgumentException("Account not found.")
            );
    }

    // Lookup restricted to the supplied account owner.
    @Transactional(readOnly = true)
    public Account getAccountForUser(Long accountNumber, User user) {
        requireAccountNumber(accountNumber);
        requireUser(user);

        return accountDAO.findByAccountNumberAndUser(accountNumber, user)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Account not found for this user."
                )
            );
    }

    // The caller must authorize access and start a database transaction.
    // Positive amounts add money; negative amounts subtract money.
    @Transactional(propagation = Propagation.MANDATORY)
    public void updateAccountBalance(
            Long accountNumber,
            BigDecimal amount) {

        requireAccountNumber(accountNumber);

        if (amount == null || amount.signum() == 0) {
            throw new IllegalArgumentException(
                "Amount must be a nonzero value."
            );
        }

        BigDecimal normalizedAmount;

        try {
            normalizedAmount = amount.setScale(
                2,
                RoundingMode.UNNECESSARY
            );
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException(
                "Amount must have no more than two decimal places."
            );
        }

        Account account = accountDAO.findById(accountNumber)
            .orElseThrow(() ->
                new IllegalArgumentException("Account not found.")
            );

        BigDecimal updatedBalance =
            account.getBalance().add(normalizedAmount);

        if (updatedBalance.signum() < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        // Matches the database column's precision of 19 and scale of 2.
        if (updatedBalance.precision() > 19) {
            throw new IllegalArgumentException(
                "The resulting balance exceeds the supported limit."
            );
        }

        account.setBalance(updatedBalance);
        accountDAO.save(account);
    }

    private static void requireUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required.");
        }
    }

    private static void requireAccountNumber(Long accountNumber) {
        if (accountNumber == null) {
            throw new IllegalArgumentException(
                "Account number is required."
            );
        }

        if (accountNumber <= 0L) {
            throw new IllegalArgumentException(
                "Account number must be greater than zero."
            );
        }
    }
}