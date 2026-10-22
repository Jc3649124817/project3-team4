package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.DTOs.AccountDTO;
import com.WAR.BankAPI.DTOs.AccountListDTO;
import com.WAR.BankAPI.models.User;
import com.WAR.BankAPI.services.AccountService;
import com.WAR.BankAPI.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/accounts")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"})
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(AccountService accountService, UserService userService) {
        this.accountService = accountService;
        this.userService = userService;
    }

    @GetMapping
    public AccountListDTO getAccounts(Authentication authentication) {
        User owner = currentUser(authentication);
        return new AccountListDTO(
            accountService.getAccountsByUser(owner).stream()
                .map(AccountDTO::from)
                .toList()
        );
    }

    @GetMapping("/{accountNumber}")
    public AccountDTO getAccount(
            @PathVariable("accountNumber") Long accountNumber,
            Authentication authentication) {
        User owner = currentUser(authentication);

        if (accountNumber == null || accountNumber <= 0L) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Account number must be greater than zero."
            );
        }

        try {
            return AccountDTO.from(
                accountService.getAccountForUser(accountNumber, owner)
            );
        } catch (IllegalArgumentException exception) {
            // Same response for another owner's account or a missing account.
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "This account is not available to you."
            );
        }
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Please log in to access your accounts."
            );
        }

        // Provisional assumption: the authenticated name is the username.
        // This is the only lookup to change if authentication uses UUID instead.
        return userService.findByUsername(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "The signed-in user could not be found."
            ));
    }

    // Keep intended statuses from becoming 500s in the current global handler.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleAccountRequestError(
            ResponseStatusException exception) {
        String message = exception.getReason() == null
            ? "Account request failed."
            : exception.getReason();
        return ResponseEntity.status(exception.getStatusCode())
            .body(Map.of("message", message));
    }
}