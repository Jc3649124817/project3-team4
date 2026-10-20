package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.TransactionDTO;
import com.WAR.BankAPI.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip security filters in this test
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    private TransactionDTO transactionDTO(int id, String type, String amount) {
        TransactionDTO tDTO = new TransactionDTO(type, id, new BigDecimal(amount),
                1L, 2L, Instant.parse("2026-10-01T12:00:00Z"));
        return tDTO;
    }

    @Test
    void testSuccessfulCall() throws Exception {
        TransactionDTO dto = transactionDTO( 1,  "deposit", "100");

        when(transactionService.getTransactions(anyInt(), anyString(), anyString(),
                any(), any(), any(), any()))
                .thenReturn(Collections.nCopies(5,dto));

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));
//                .andExpect(jsonPath("$[0].transactionId").value(1))
//                .andExpect(jsonPath("$[0].type").value("deposit"));
    }

    @Test
    void testSuccessfulCallNoTransactions() throws Exception {
        when(transactionService.getTransactions(anyInt(), anyString(), anyString(),
                any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testSuccessfulNumTransactionsParameters() throws Exception {

        TransactionDTO dto = transactionDTO( 1,  "deposit", "100");

        when(transactionService.getTransactions(anyInt(), anyString(), anyString(),
                any(), any(), any(), any()))
                .thenReturn(Collections.nCopies(10,dto));

        mockMvc.perform(get("/transactions?numTransactions=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10));

    }

    @Test
    void testWrongTypeParameters() throws Exception {

        TransactionDTO dto = transactionDTO( 1,  "deposit", "100");

        when(transactionService.getTransactions(anyInt(), anyString(), anyString(),
                any(), any(), any(), any()))
                .thenReturn(Collections.nCopies(10,dto));

        mockMvc.perform(get("/transactions?numTransactions=10&minAmount=abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidRangeParameters() throws Exception {

        TransactionDTO dto = transactionDTO( 1,  "deposit", "100");

        when(transactionService.getTransactions(anyInt(), anyString(), anyString(),
                any(), any(), any(), any()))
                .thenThrow(new InvalidRequestException("Min amount must be equal or lower than max amount."));

        mockMvc.perform(get("/transactions?minAmount=100&maxAmount=10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidTransactionTypeParameters() throws Exception {

        TransactionDTO dto = transactionDTO( 1,  "deposit", "100");

        when(transactionService.getTransactions(anyInt(), anyString(), anyString(),
                any(), any(), any(), any()))
                .thenThrow(new InvalidRequestException("Invalid transaction type entered."));

        mockMvc.perform(get("/transactions?type=all"))
                .andExpect(status().isBadRequest());
    }











}