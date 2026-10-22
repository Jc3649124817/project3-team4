package com.WAR.BankAPI.models;

public enum TransactionType {
    WITHDRAW("withdraw"),
    DEPOSIT("deposit"),
    TRANSFER("transfer");


    private final String property;

    TransactionType(String property) {
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static boolean contains(String input) {
        for (TransactionType type : TransactionType.values()) {
            if (type.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
}
