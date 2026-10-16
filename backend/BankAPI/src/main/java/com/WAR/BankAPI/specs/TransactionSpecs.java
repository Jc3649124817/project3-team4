package com.WAR.BankAPI.Filters;

import com.WAR.BankAPI.models.Transaction;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.math.BigDecimal;

class TransactionSpecs {

    private TransactionSpecs() {}

    public static PredicateSpecification<Transaction> amountAtLeast(BigDecimal minAmount) {
        if (minAmount == null) return null;
        return (from, builder) -> builder.greaterThanOrEqualTo(from.get("amount"), minAmount);
    }

    public static PredicateSpecification<Transaction> amountAtMost(BigDecimal maxAmount) {
        if (maxAmount == null) return null;
        return (from, builder) -> builder.lessThanOrEqualTo(from.get("amount"), maxAmount);
    }

    public static PredicateSpecification<Transaction> hasDestinationId(String destinationId) {
        if (destinationId == null) return null;
        return (from, builder) -> builder.equal(from.get("destinationId"), destinationId);
    }

    public static PredicateSpecification<Transaction> hasOriginId(String originId) {
        if (originId == null) return null;
        return (from, builder) -> builder.equal(from.get("originId"), originId);
    }

}