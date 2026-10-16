package com.WAR.BankAPI.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Fake User Model: replace with Connor's model later.
@Entity
@Table(name="users")
public class User {
    @Id
    private int userId;
}
