package com.WAR.BankAPI.services;

import com.WAR.BankAPI.DTOs.LoginDTO;
import com.WAR.BankAPI.DTOs.UserDTO;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.WAR.BankAPI.services.AccountService;

@Service //1 of the 4 stereotype annotations - makes this Class a bean
public class AuthService {

    //The controllers call services, which call the DAOs (data access objects)

    //The Service needs the DAO. They're both beans, so let's CONSTRUCTOR INJECT
    private final UserDAO userDAO;
    //We also need the UserService to find users by username
    private final UserService userService;
    //We also need the AccountService to create default accounts
    private final AccountService accountService;

    @Autowired
    public AuthService(UserDAO userDAO, UserService userService, AccountService accountService) {
        this.userDAO = userDAO;
        this.userService = userService;
        this.accountService = accountService;
    }

    //Login - calls the UserDAO's findByUsernameAndPassword()
    public User login(LoginDTO loginDTO){

        // Basic input validation
        String inputtedUsername = loginDTO.username();
        String inputtedPassword = loginDTO.password();

        if (inputtedUsername == null || inputtedUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }
        if (inputtedPassword == null || inputtedPassword.isEmpty()) {
            throw new IllegalArgumentException("Pasword cannot be blank");
        }
 
        // IF the username-password pair exists, return it.
        // Otherwise, throw an exception saying the credentials are invalid
        return userDAO.findByUsernameAndPassword(
                loginDTO.username(), loginDTO.password()
        ).orElseThrow(() -> new IllegalArgumentException("Login Credentials Invalid"));

    }

    //Insert User - calls the UserDAO's save() method
    @Transactional
    public User insertUser(UserDTO newUser){

        //Some basic input validation (make sure the user fields are valid)

        //Check if a valid username was passed in
        if(newUser.username().isBlank()){
            throw new IllegalArgumentException("Username must be present!");
        }

        //Check if username already exists
        //Using our custom UserDAO method findByUsername()
        if(userService.findByUsername(newUser.username()).isPresent()){
            throw new IllegalArgumentException("Username taken!");
        }

        // Create a User object from the inputted UserDTO
        User input = new User(newUser.firstName(), newUser.lastName(), newUser.email(), newUser.phoneNumber(), newUser.username(), newUser.password());

        //save() is used for inserts AND updates
            //if the ID doesn't exist, it inserts a new record
            //if the ID DOES exist, it updates it
        User result = userDAO.save(input);
        accountService.createDefaultAccounts(input);
        return result;
    }
}
