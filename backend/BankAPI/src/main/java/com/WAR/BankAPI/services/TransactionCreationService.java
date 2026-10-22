package com.WAR.BankAPI.services;

import com.WAR.BankAPI.DTOs.TransactionRequestDTO;
import com.WAR.BankAPI.DTOs.TransactionDTO;
import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.Account;
import com.WAR.BankAPI.models.Transaction;
import com.WAR.BankAPI.models.TransactionType;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.TransactionDAO;
import com.WAR.BankAPI.repos.UserDAO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;

@Service
public class TransactionCreationService {
    private final AccountService accountService;
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;

    public TransactionCreationService(AccountService accountService, TransactionDAO transactionDAO, UserDAO userDAO) {
        this.accountService = accountService;
        this.transactionDAO = transactionDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public TransactionDTO putTransaction(TransactionRequestDTO request, String username) {
        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required.");
        }
        User user = userDAO.findByUsername(username).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login is required."));

        if (request == null || request.transactionType() == null) {
            throw new InvalidRequestException("Transaction type is required.");
        }
        TransactionType type;
        try {
            type = TransactionType.valueOf(request.transactionType().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException("Transaction type must be Deposit, Withdraw, or Transfer.");
        }
        BigDecimal amount = validateAmount(request.amount());
        Long origin = request.originId();
        Long destination = request.destinationId();

        switch (type) {
            case DEPOSIT -> {
                if (origin != null) {
                    throw new InvalidRequestException("A deposit must not have an origin account.");
                }
                requireAccountNumber(destination);
            }
            case WITHDRAW -> {
                if (destination != null) {
                    throw new InvalidRequestException("A withdrawal must not have a destination account.");
                }
                requireAccountNumber(origin);
            }
            case TRANSFER -> {
                requireAccountNumber(origin);
                requireAccountNumber(destination);
                if (origin.equals(destination)) {
                    throw new InvalidRequestException("Transfer accounts must be different.");
                }
            }
        }

        // resolve and authorize accounts before changing either balance
        if (origin != null) {
            requireOwner(findAccount(origin), user);
        }
        if (destination != null) {
            Account target = findAccount(destination);
            if (type == TransactionType.DEPOSIT) {
                requireOwner(target, user);
            }
        }

        try {
            if (origin != null) {
                accountService.updateAccountBalance(origin, amount.negate());
            }
            if (destination != null) {
                accountService.updateAccountBalance(destination, amount);
            }
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestException(exception.getMessage());
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionType(type.property());
        transaction.setAmount(amount);
        transaction.setOriginId(origin);
        transaction.setDestinationId(destination);
        transaction.setUser(user);
        // flush, so the response includes the generated ID and creation timestamp
        return TransactionDTO.from(transactionDAO.saveAndFlush(transaction));
    }

    private Account findAccount(Long number) {
        try {
            return accountService.getAccountByAccountNumber(number);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found.");
        }
    }

    private static void requireOwner(Account account, User user) {
        if (user.getUserId() == null || account.getUser() == null
                || !Objects.equals(account.getUser().getUserId(), user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account does not belong to the logged-in user.");
        }
    }

    private static void requireAccountNumber(Long number) {
        if (number == null || number <= 0) {
            throw new InvalidRequestException("A positive account number is required.");
        }
    }

    private static BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidRequestException("Amount must be greater than zero.");
        }
        try {
            amount = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new InvalidRequestException("Amount must have no more than two decimal places.");
        }
        if (amount.precision() > 19) {
            throw new InvalidRequestException("Amount exceeds the supported limit.");
        }
        return amount;
    }
}
