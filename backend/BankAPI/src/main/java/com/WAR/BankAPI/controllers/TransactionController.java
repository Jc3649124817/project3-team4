package com.WAR.BankAPI.controllers;

import com.WAR.BankAPI.models.Transaction;
import com.WAR.BankAPI.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController //makes the class a bean AND turns responses into JSON
@RequestMapping("/transactions") //all requests to /transactions to route to this Controller
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @GetMapping
    public ResponseEntity<List<Transaction>> searchTransactions(
            @RequestParam(defaultValue = "5") int numTransactions,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) Long accountId) {


        return ResponseEntity.ok().body(transactionService.getTransactions( numTransactions,
                sortBy, direction,
                type, minAmount, maxAmount, accountId));
    }


}
