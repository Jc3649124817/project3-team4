package com.WAR.BankAPI.services;

import com.WAR.BankAPI.DTOs.LoginDTO;
import com.WAR.BankAPI.DTOs.UserDTO;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service //1 of the 4 stereotype annotations - makes this Class a bean
public class AuthService {

    //The controllers call services, which call the DAOs (data access objects)

    //The Service needs the DAO. They're both beans, so let's CONSTRUCTOR INJECT
    private final UserDAO userDAO;
    //We also need the UserService to find users by username
    private final UserService userService;

    @Autowired
    public AuthService(UserDAO userDAO, UserService userService) {
        this.userDAO = userDAO;
        this.userService = userService;
    }

    //Login - calls the UserDAO's findByUsernameAndPassword()
    public User login(LoginDTO loginDTO){

        // Basic input validation
        String inputtedUsername = loginDTO.getUsername();
        String inputtedPassword = loginDTO.getPassword();

        if (inputtedUsername == null || inputtedUsername.trim().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (inputtedPassword == null || inputtedPassword.isBlank()) {
            throw new IllegalArgumentException("Pasword cannot be blank");
        }
 
        // IF the username-password pair exists, return it.
        // Otherwise, throw an exception saying the credentials are invalid
        return userDAO.findByUsernameAndPassword(
                loginDTO.getUsername(), loginDTO.getPassword()
        ).orElseThrow(() -> new IllegalArgumentException("Login Credentials Invalid"));

    }

    //Insert User - calls the UserDAO's save() method
    public User insertUser(UserDTO newUser){

        //Some basic input validation (make sure the user fields are valid)

        //Check if a valid username was passed in
        if(newUser.getUsername().isBlank()){
            throw new IllegalArgumentException("Username must be present!");
        }

        //Check if username already exists
        //Using our custom UserDAO method findByUsername()
        if(userService.findByUsername(newUser.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username taken!");
        }

        // Create a User object from the inputted UserDTO
        User input = new User(newUser.getFirstName(), newUser.getLastName(), newUser.getEmail(), newUser.getPhoneNumber(), newUser.getUsername(), newUser.getPassword());

        //save() is used for inserts AND updates
            //if the ID doesn't exist, it inserts a new record
            //if the ID DOES exist, it updates it
        return userDAO.save(input);
    }
}
