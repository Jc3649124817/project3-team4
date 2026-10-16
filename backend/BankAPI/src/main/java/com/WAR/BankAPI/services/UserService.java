package com.WAR.BankAPI.services;

import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service //make this class a bean!
public class UserService {

    //Constructor Inject the UserDAO

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> findByUsername(String username){

        return userDAO.findByUsername(username);

    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {

        return userDAO.findByUsernameAndPassword(username, password);

    }


}
