package com.WAR.BankAPI.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangePasswordDTO(
        @JsonProperty("CurrentPassword") String currentPassword,
        @JsonProperty("NewPassword") String newPassword
) {}