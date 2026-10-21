package com.WAR.BankAPI.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice   // applies to every @RestController in the app
public class GlobalExceptionHandler {


    // Your own validation failures from the service layer
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalidRequest(InvalidRequestException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Wrong type in a query param, e.g. ?minAmount=abc or ?accountId=xyz
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    // Safety net: anything unexpected becomes a 500 without leaking a stack trace
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpected(Exception e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}