package com.WAR.BankAPI.services;

import com.WAR.BankAPI.DTOs.TransactionRequestDTO;
import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.*;
import com.WAR.BankAPI.repos.TransactionDAO;
import com.WAR.BankAPI.repos.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionCreationServiceTest {
    @Mock AccountService accountService;
    @Mock TransactionDAO transactionDAO;
    @Mock UserDAO userDAO;
    @InjectMocks TransactionCreationService service;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "userId", Integer.valueOf(1));
    }

    private void loggedIn() {
        when(userDAO.findByUsername("alice")).thenReturn(Optional.of(user));
    }

    private void account(long number, User owner) {
        when(accountService.getAccountByAccountNumber(number))
                .thenReturn(new Account(owner, AccountType.CHECKING));
    }

    private void savedTransaction() {
        when(transactionDAO.saveAndFlush(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setTransactionId(7);
            t.setDate(Instant.parse("2026-10-22T12:00:00Z"));
            return t;
        });
    }

    @Test
    void depositCreditsOwnedDestinationAndRecordsServerGeneratedFields() {
        loggedIn();
        account(2, user);
        savedTransaction();
        var result = service.putTransaction(request("dEpOsIt", "25.50", null, 2L), "alice");
        assertEquals(7, result.transactionId());
        assertEquals("Deposit", result.type());
        assertNotNull(result.date());
        assertEquals(new BigDecimal("25.50"), result.amount());
        verify(accountService).updateAccountBalance(2L, new BigDecimal("25.50"));
        verify(transactionDAO).saveAndFlush(argThat(t -> t.getUser() == user
                && t.getOriginId() == null && t.getDestinationId().equals(2L)
                && t.getTransactionType().equals("deposit")));
    }

    @Test
    void withdrawalDebitsOwnedOrigin() {
        loggedIn();
        account(1, user);
        savedTransaction();
        service.putTransaction(request("Withdraw", "10", 1L, null), "alice");
        verify(accountService).updateAccountBalance(1L, new BigDecimal("-10.00"));
        verify(accountService, times(1)).updateAccountBalance(any(), any());
    }

    @Test
    void transferCanCreditAnotherUserButMustDebitOwnedOrigin() {
        loggedIn();
        account(1, user);
        account(2, new User());
        savedTransaction();
        service.putTransaction(request("Transfer", "10", 1L, 2L), "alice");
        var order = inOrder(accountService, transactionDAO);
        order.verify(accountService).updateAccountBalance(1L, new BigDecimal("-10.00"));
        order.verify(accountService).updateAccountBalance(2L, new BigDecimal("10.00"));
        order.verify(transactionDAO).saveAndFlush(any());
    }

    static Stream<TransactionRequestDTO> invalidRequests() {
        return Stream.of(
                request(null, "10", 1L, 2L), request("invalid", "10", 1L, 2L),
                request("Deposit", null, null, 2L), request("Deposit", "0", null, 2L),
                request("Deposit", "-1", null, 2L), request("Deposit", "1.001", null, 2L),
                request("Deposit", "100000000000000000", null, 2L),
                request("Deposit", "10", 1L, 2L), request("Deposit", "10", null, null),
                request("Deposit", "10", null, -1L), request("Withdraw", "10", 1L, 2L),
                request("Withdraw", "10", null, null), request("Transfer", "10", 1L, 1L),
                request("Transfer", "10", null, 2L), request("Transfer", "10", 1L, 0L));
    }

    @ParameterizedTest
    @MethodSource("invalidRequests")
    void rejectsInvalidRequestsBeforeAccountAccess(TransactionRequestDTO request) {
        loggedIn();
        assertThrows(InvalidRequestException.class, () -> service.putTransaction(request, "alice"));
        verifyNoInteractions(accountService, transactionDAO);
    }

    @Test
    void rejectsUnauthenticatedOrUnknownUser() {
        var request = request("Deposit", "10", null, 2L);
        assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ResponseStatusException.class,
                () -> service.putTransaction(request, null)).getStatusCode());
        assertEquals(HttpStatus.UNAUTHORIZED, assertThrows(ResponseStatusException.class,
                () -> service.putTransaction(request, "unknown")).getStatusCode());
        verifyNoInteractions(accountService, transactionDAO);
    }

    @Test
    void rejectsAnotherOwnersWithdrawalAndDeposit() {
        loggedIn();
        User other = new User();
        ReflectionTestUtils.setField(other, "userId", Integer.valueOf(2));
        account(1, other);
        for (var request : new TransactionRequestDTO[]{
                request("Withdraw", "10", 1L, null), request("Deposit", "10", null, 1L),
                request("Transfer", "10", 1L, 2L)}) {
            assertEquals(HttpStatus.FORBIDDEN, assertThrows(ResponseStatusException.class,
                    () -> service.putTransaction(request, "alice")).getStatusCode());
        }
        verify(accountService, never()).updateAccountBalance(any(), any());
        verifyNoInteractions(transactionDAO);
    }

    @Test
    void checksDestinationExistsBeforeDebitingTransfer() {
        loggedIn();
        account(1, user);
        when(accountService.getAccountByAccountNumber(2L)).thenThrow(new IllegalArgumentException());
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> service.putTransaction(request("Transfer", "10", 1L, 2L), "alice")).getStatusCode());
        verify(accountService, never()).updateAccountBalance(any(), any());
        verifyNoInteractions(transactionDAO);
    }

    @Test
    void insufficientFundsDoesNotCreditDestinationOrSaveHistory() {
        loggedIn();
        account(1, user);
        account(2, user);
        doThrow(new IllegalArgumentException("Insufficient funds."))
                .when(accountService).updateAccountBalance(1L, new BigDecimal("-10.00"));
        assertEquals("Insufficient funds.", assertThrows(InvalidRequestException.class,
                () -> service.putTransaction(request("Transfer", "10", 1L, 2L), "alice")).getMessage());
        verify(accountService, never()).updateAccountBalance(2L, new BigDecimal("10.00"));
        verifyNoInteractions(transactionDAO);
    }

    private static TransactionRequestDTO request(String type, String amount, Long origin, Long destination) {
        return new TransactionRequestDTO(type, amount == null ? null : new BigDecimal(amount), origin, destination);
    }
}


