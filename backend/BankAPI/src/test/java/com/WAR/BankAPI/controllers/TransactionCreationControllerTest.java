package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.DTOs.TransactionRequestDTO;
import com.WAR.BankAPI.DTOs.TransactionDTO;
import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.services.TransactionCreationService;
import org.junit.jupiter.api.Test;
import com.WAR.BankAPI.jwt.JwtFilter;
import com.WAR.BankAPI.jwt.JwtUtil;
import com.WAR.BankAPI.jwt.SecurityConfig;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionCreationController.class)
@Import({SecurityConfig.class, JwtFilter.class, JwtUtil.class})
class TransactionCreationControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean TransactionCreationService service;
    private static final String BODY = """
            {"TransactionType":"Transfer","Amount":25.50,"OriginID":1,"DestinationID":2}
            """;

    @Test
    @WithMockUser(username = "alice")
    void createsTransactionUsingAuthenticatedUsernameAndContractFields() throws Exception {
        when(service.putTransaction(any(), eq("alice"))).thenReturn(new TransactionDTO(
                7, "Transfer", new BigDecimal("25.50"), 1L, 2L,
                Instant.parse("2026-10-22T12:00:00Z")));
        mvc.perform(post("/transactions").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ID").value(7))
                .andExpect(jsonPath("$.TransactionType").value("Transfer"))
                .andExpect(jsonPath("$.Amount").value(25.50))
                .andExpect(jsonPath("$.OriginID").value(1))
                .andExpect(jsonPath("$.DestinationID").value(2))
                .andExpect(jsonPath("$.Date").exists())
                .andExpect(jsonPath("$.user").doesNotExist());
        verify(service).putTransaction(new TransactionRequestDTO("Transfer", new BigDecimal("25.50"), 1L, 2L), "alice");
    }

    @Test
    void anonymousRequestCannotCreateTransaction() throws Exception {
        mvc.perform(post("/transactions").with(csrf()).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().is4xxClientError());
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void malformedBodyReturns400() throws Exception {
        mvc.perform(post("/transactions").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser
    void validationFailureReturns400() throws Exception {
        when(service.putTransaction(any(), anyString())).thenThrow(new InvalidRequestException("Insufficient funds."));
        mvc.perform(post("/transactions").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest()).andExpect(content().string("Insufficient funds."));
    }

    @Test
    @WithMockUser
    void forbiddenAccountReturns403() throws Exception {
        when(service.putTransaction(any(), anyString())).thenThrow(
                new ResponseStatusException(HttpStatus.FORBIDDEN, "Account does not belong to the logged-in user."));
        mvc.perform(post("/transactions").with(csrf()).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
    }

}

