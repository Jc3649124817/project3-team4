package com.WAR.BankAPI.models;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

import static org.springframework.util.StringUtils.capitalize;

public record TransactionDTO(
        @JsonProperty("ID") Integer transactionId,
        @JsonProperty("TransactionType") String type,
        @JsonProperty("Amount") BigDecimal amount,
        @JsonProperty("OriginID") Long originId,
        @JsonProperty("DestinationID") Long destinationId,
        @JsonProperty("Date") Instant date
) {
    public static TransactionDTO from(Transaction t) {
        return new TransactionDTO(
                t.getTransactionId(),
                capitalize(t.getTransactionType()),
                t.getAmount(),
                t.getOriginId(),
                t.getDestinationId(),
                t.getDate()
        );
    }
}

