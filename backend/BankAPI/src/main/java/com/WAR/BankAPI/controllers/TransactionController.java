package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.DTOs.TransactionDTO;
import com.WAR.BankAPI.DTOs.TransactionListDTO;
import com.WAR.BankAPI.models.*;
import com.WAR.BankAPI.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4200"}, allowedHeaders = "*")
@RestController //makes the class a bean AND turns responses into JSON
@RequestMapping("/transactions") //all requests to /transactions to route to this Controller
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<TransactionListDTO> searchTransactions(
            @RequestParam(defaultValue = "5") int numTransactions,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) Long accountId, Authentication authentication) {
        //TODO: integrate with JWT, if expired/unauthenticated, pass a 401: unauthorized.

        String username = authentication.getName();
        if(numTransactions <= 0 || numTransactions > 100){
            numTransactions = 5;
        }
        if(!direction.equals("desc") && !direction.equals("asc")){
            direction = "desc";
        }
        if(!SortField.contains(sortBy)){
            sortBy = "date";
        }


        List<TransactionDTO> transactions =  transactionService.getTransactions( numTransactions,
                sortBy, direction,
                type, minAmount, maxAmount, accountId, username);

        return ResponseEntity.ok(new TransactionListDTO(transactions));
    }


}
