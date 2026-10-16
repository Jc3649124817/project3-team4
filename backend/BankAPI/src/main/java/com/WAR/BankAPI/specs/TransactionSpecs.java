package com.WAR.BankAPI.specs;

import com.WAR.BankAPI.models.Transaction;
import org.springframework.data.jpa.domain.PredicateSpecification;

import java.math.BigDecimal;

public class TransactionSpecs {

    private TransactionSpecs() {}

    public static PredicateSpecification<Transaction> hasType(String type) {
        if (type == null) return null;
        return (from, builder) -> builder.equal(from.get("type"), type);
    }


    public static PredicateSpecification<Transaction> amountAtLeast(BigDecimal minAmount) {
        if (minAmount == null) return null;
        return (from, builder) -> builder.greaterThanOrEqualTo(from.get("amount"), minAmount);
    }

    public static PredicateSpecification<Transaction> amountAtMost(BigDecimal maxAmount) {
        if (maxAmount == null) return null;
        return (from, builder) -> builder.lessThanOrEqualTo(from.get("amount"), maxAmount);
    }

    public static PredicateSpecification<Transaction> involvesAccount(Long accountId) {
        if (accountId == null) return null;
        return (from, builder) -> builder.or(
                builder.equal(from.get("destinationId"), accountId),
                builder.equal(from.get("originId"), accountId)
        );
    }

}