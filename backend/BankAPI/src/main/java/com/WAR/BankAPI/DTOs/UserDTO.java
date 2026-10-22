package com.WAR.BankAPI.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

/*Remember a DTO is a Data Transfer Object - it helps us move certain data around

I like having a model for certain things that will never hit the DB.
    -In this case username/login credentials. We'll pass this around during login

Yes, you could just have a constructor in User that only has username/password instead */
public record UserDTO(
    @JsonProperty("FirstName") String firstName,
    @JsonProperty("LastName") String lastName,
    @JsonProperty("Email") String email,
    @JsonProperty("PhoneNumber") String phoneNumber,
    @JsonProperty("Username") String username,
    @JsonProperty("Password") String password,
    @JsonProperty("Password2") String password2
) {}