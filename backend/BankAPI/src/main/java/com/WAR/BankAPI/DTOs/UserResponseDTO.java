package com.WAR.BankAPI.DTOs;

import com.WAR.BankAPI.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponseDTO(
        @JsonProperty("FirstName") String firstName,
        @JsonProperty("LastName") String lastName,
        @JsonProperty("Email") String email,
        @JsonProperty("PhoneNumber") String phoneNumber,
        @JsonProperty("Username") String username
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUsername()
        );
    }
}