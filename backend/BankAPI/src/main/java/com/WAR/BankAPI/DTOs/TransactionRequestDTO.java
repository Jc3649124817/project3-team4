package com.WAR.BankAPI.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record TransactionRequestDTO(
        @JsonProperty("TransactionType") String transactionType,
        @JsonProperty("Amount") BigDecimal amount,
        @JsonProperty("OriginID") Long originId,
        @JsonProperty("DestinationID") Long destinationId) {
}
