package com.WAR.BankAPI.DTOs;


import com.WAR.BankAPI.models.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record AccountDTO(
        @JsonProperty("AccountNumber") Long accountNumber,
        @JsonProperty("AccountType") String accountType,
        @JsonProperty("Balance") BigDecimal balance
) {
    public static AccountDTO from(Account a) {
        return new AccountDTO(
                a.getAccountNumber(),
                capitalize(a.getAccountType().name()),
                a.getBalance()
        );
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
