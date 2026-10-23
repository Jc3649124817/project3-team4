package com.WAR.BankAPI.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateProfileDTO(
        @JsonProperty("FirstName") String firstName,
        @JsonProperty("LastName") String lastName,
        @JsonProperty("Email") String email,
        @JsonProperty("PhoneNumber") String phoneNumber
) {}