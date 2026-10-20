package com.WAR.BankAPI.models;


import java.math.BigDecimal;
import java.time.Instant;

public record TransactionDTO(String type, Integer transactionId, BigDecimal amount,Long originId, Long destinationId, Instant date) {
    public static TransactionDTO from(Transaction t) {
        return new TransactionDTO(
                t.getType(),
                t.getTransactionId(),
                t.getAmount(),
                t.getOriginId(),
                t.getDestinationId(),
                t.getDate()
        );
    }
}

