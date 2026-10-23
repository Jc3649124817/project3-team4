package com.WAR.BankAPI.services;


import com.WAR.BankAPI.DTOs.ChangePasswordDTO;
import com.WAR.BankAPI.DTOs.UpdateProfileDTO;
import com.WAR.BankAPI.exceptions.InvalidRequestException;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.repos.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public User getByUsername(String username) {
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
    }

    @Transactional
    public User updateProfile(String username, UpdateProfileDTO dto) {
        User user = getByUsername(username);

        if (dto.firstName() != null) {
            if (dto.firstName().isBlank()) {
                throw new InvalidRequestException("First name cannot be blank.");
            }
            user.setFirstName(dto.firstName().trim());
        }
        if (dto.lastName() != null) {
            if (dto.lastName().isBlank()) {
                throw new InvalidRequestException("Last name cannot be blank.");
            }
            user.setLastName(dto.lastName().trim());
        }
        if (dto.email() != null) {
            if (dto.email().isBlank() || !dto.email().contains("@")) {
                throw new InvalidRequestException("Invalid email address.");
            }
            user.setEmail(dto.email().trim());
        }
        if (dto.phoneNumber() != null) {
            if (dto.phoneNumber().isBlank()) {
                throw new InvalidRequestException("Phone number cannot be blank.");
            }
            user.setPhoneNumber(dto.phoneNumber().trim());
        }

        return userDAO.save(user);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordDTO dto) {
        User user = getByUsername(username);

        if (dto.currentPassword() == null || !dto.currentPassword().equals(user.getPassword())) {
            throw new InvalidRequestException("Failed Password Change: Current password is incorrect.");
        }
        if (dto.newPassword() == null || dto.newPassword().length() < 8) {
            throw new InvalidRequestException("New password must be at least 8 characters.");
        }
        if (dto.newPassword().equals(dto.currentPassword())) {
            throw new InvalidRequestException("New password must differ from your current password.");
        }

        user.setPassword(dto.newPassword());
        userDAO.save(user);
    }
}
