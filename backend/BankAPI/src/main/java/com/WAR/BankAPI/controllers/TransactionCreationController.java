package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.DTOs.TransactionRequestDTO;
import com.WAR.BankAPI.DTOs.TransactionDTO;
import com.WAR.BankAPI.services.TransactionCreationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"}, allowedHeaders = "*")
public class TransactionCreationController {
    private final TransactionCreationService transactionCreationService;

    public TransactionCreationController(TransactionCreationService transactionCreationService) {
        this.transactionCreationService = transactionCreationService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> putTransaction(
            @RequestBody TransactionRequestDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                transactionCreationService.putTransaction(request, authentication.getName()));
    }
}
