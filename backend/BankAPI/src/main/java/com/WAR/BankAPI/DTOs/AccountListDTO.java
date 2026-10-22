package com.WAR.BankAPI.DTOs;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AccountListDTO(
        @JsonProperty("Accounts") List<AccountDTO> accounts
) {}