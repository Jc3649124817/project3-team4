package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.DTOs.ChangePasswordDTO;
import com.WAR.BankAPI.DTOs.UpdateProfileDTO;
import com.WAR.BankAPI.DTOs.UserResponseDTO;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getMe(Authentication authentication) {
        User user = userService.getByUsername(authentication.getName());
        return ResponseEntity.ok(UserResponseDTO.from(user));
    }

    @PutMapping
    public ResponseEntity<UserResponseDTO> updateMe(Authentication authentication,
                                                    @RequestBody UpdateProfileDTO dto) {
        User updated = userService.updateProfile(authentication.getName(), dto);
        return ResponseEntity.ok(UserResponseDTO.from(updated));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(authentication.getName(), dto);
        return ResponseEntity.ok().build();
    }
}