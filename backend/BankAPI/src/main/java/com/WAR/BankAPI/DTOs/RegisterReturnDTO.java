package com.WAR.BankAPI.DTOs;

public class RegisterReturnDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;

    public RegisterReturnDTO() {
    }

    public RegisterReturnDTO(String firstName, String lastName, String email,
                   String phoneNumber, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "RegisterReturnDTO [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", phoneNumber=" + phoneNumber + ", username=" + username + "]";
    }
}
