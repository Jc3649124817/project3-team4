package com.WAR.BankAPI.models;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum SortField {

    DATE("date"),
    AMOUNT("amount"),
    TYPE("type");

    private final String property;

    SortField(String property) {
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static boolean contains(String input) {
        for (SortField field : SortField.values()) {
            if (field.name().equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;

    }


}
