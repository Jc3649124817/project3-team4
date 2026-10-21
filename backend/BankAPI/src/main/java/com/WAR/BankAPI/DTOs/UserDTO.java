package com.WAR.BankAPI.DTOs;

/*Remember a DTO is a Data Transfer Object - it helps us move certain data around

I like having a model for certain things that will never hit the DB.
    -In this case username/login credentials. We'll pass this around during login

Yes, you could just have a constructor in User that only has username/password instead */
public class UserDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;
    private String password2;

    public UserDTO() {
    }

    public UserDTO(String firstName, String lastName, String email,
                   String phoneNumber, String username, String password,
                   String password2) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.password2 = password2;
    }

    // Boilerplate getters and setters

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    @Override
    public String toString() {
        return "UserDTO [firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + ", phoneNumber="
                + phoneNumber + ", username=" + username + ", password=" + password + ", password2=" + password2 + "]";
    }
}