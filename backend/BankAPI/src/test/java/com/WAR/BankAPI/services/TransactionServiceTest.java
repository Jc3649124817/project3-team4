package com.WAR.BankAPI.services;

import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.Transaction;
import com.WAR.BankAPI.DTOs.TransactionDTO;
import com.WAR.BankAPI.repos.TransactionDAO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionDAO transactionDAO;
    @InjectMocks
    private TransactionService transactionService;



    private Transaction transaction(int id, String type, String amount) {
        Transaction t = new Transaction();
        t.setTransactionId(id);
        t.setTransactionType(type);
        t.setAmount(new BigDecimal(amount));
        t.setOriginId(1L);
        t.setDestinationId(2L);
        t.setDate(Instant.parse("2026-10-01T12:00:00Z"));
        return t;
    }

    private void daoReturns(List<Transaction> result) {
        when(transactionDAO.findBy(any(PredicateSpecification.class), any()))
                .thenReturn(result);
    }

    private List<TransactionDTO> callWith(String type, BigDecimal min, BigDecimal max) {
        return transactionService.getTransactions(5, "date", "desc", type, min, max, null);
    }



    @Test
    void testValidInputs() throws Exception{
        daoReturns(List.of());
        assertThat(callWith(null, new BigDecimal("50"), new BigDecimal("50"))).isEmpty();
        verify(transactionDAO).findBy(any(PredicateSpecification.class), any());
    }

    @Test
    void testReturnsTransactionDTOs() {
        Transaction t = new Transaction();
        t.setTransactionId(1);
        t.setTransactionType("deposit");
        t.setAmount(new BigDecimal("100.00"));


        when(transactionDAO.findBy(any(PredicateSpecification.class), any()))
                .thenReturn(Collections.nCopies(5, t));

        List<TransactionDTO> result =
                transactionService.getTransactions(5, "date", "desc", null, null, null, null);

        assertEquals(5, result.size());
        assertEquals(1, result.get(0).transactionId());
        assertEquals("deposit", result.get(0).type());
        assertEquals(0, new BigDecimal("100.00").compareTo(result.get(0).amount()));
    }

    @Test
    void testReturnsFewerTransactionDTOs() {
        Transaction t = new Transaction();
        t.setTransactionId(1);
        t.setTransactionType("deposit");
        t.setAmount(new BigDecimal("100.00"));


        when(transactionDAO.findBy(any(PredicateSpecification.class), any()))
                .thenReturn(Collections.nCopies(1, t));

        List<TransactionDTO> result =
                transactionService.getTransactions(5, "date", "desc", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).transactionId());
        assertEquals("deposit", result.get(0).type());
        assertEquals(0, new BigDecimal("100.00").compareTo(result.get(0).amount()));
    }




    @Test
    void testInvalidAmountRange() throws  Exception{
        assertThatThrownBy( () -> callWith(null, new BigDecimal("100"), new BigDecimal("10")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Min amount must be equal or lower than max amount.");

    }

    @Test
    void testMinAmountZero() throws  Exception{
        assertThatThrownBy( () -> callWith(null, new BigDecimal("0"), new BigDecimal("1000")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Min amount must be greater than 0.");

    }

    @Test
    void testValidTransactionType() throws Exception{
        daoReturns(List.of());
        assertThat(callWith("deposit", new BigDecimal("50"), new BigDecimal("50"))).isEmpty();
        verify(transactionDAO).findBy(any(PredicateSpecification.class), any());
    }

    @Test
    void testInvalidTransactionType() throws Exception{
        assertThatThrownBy( () -> callWith("Apple", new BigDecimal("100"), new BigDecimal("1000")))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Invalid transaction type entered.");

    }


}
