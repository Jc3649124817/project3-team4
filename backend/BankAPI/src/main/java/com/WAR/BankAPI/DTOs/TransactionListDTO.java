package com.WAR.BankAPI.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TransactionListDTO(
        @JsonProperty("Transactions") List<TransactionDTO> transactions
) {}
