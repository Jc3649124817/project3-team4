package com.WAR.BankAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity //This tells Spring Data that this Class is meant to be a DB entity (DB table)
@Table(name = "users") //This helps us set table configs like the name
public class User {

    //Make sure all variable names are camelCase - this will help Spring Data
    //Spring Data doesn't play nice with snake_case, and the errors will be super vague

    @Id //Sets this field as the Primary Key (PK)
    @GeneratedValue(strategy = GenerationType.UUID) //This is what makes the PK autoincrement
    private UUID userId;

    //@Column - this is not needed to make a field a column!!!
        //BUT, we need it to apply constraints
    
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String username;
    private String password;


    //boilerplate---------------------------------

    //no args, all args minus ID, all args, getter/setter, toString

    public User() {
    }

    public User(String firstName, String lastName, String email,
                String phoneNumber, String username, String password
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

    public UUID getUserId() {
        return userId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
                + ", phoneNumber=" + phoneNumber + ", username=" + username + "]";
    }
}
