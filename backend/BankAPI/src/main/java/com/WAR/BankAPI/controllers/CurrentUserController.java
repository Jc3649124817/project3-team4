package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class CurrentUserController {
    private final UserService userService;

    public CurrentUserController(UserService userService) {
        this.userService = userService;
    }

    // /auth/** is public, so explicitly check authentication here.
    @GetMapping("/auth/me")
    public ResponseEntity<?> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Please log in."));
        }
        var owner = userService.findByUsername(authentication.getName());
        if (owner.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "The signed-in user was not found."));
        }
        var user = owner.get();
        // Return profile information only, never the entity or password.
        return ResponseEntity.ok(Map.of(
            "FirstName", user.getFirstName(),
            "LastName", user.getLastName(),
            "Email", user.getEmail(),
            "PhoneNumber", user.getPhoneNumber(),
            "Username", user.getUsername()
        ));
    }
}
