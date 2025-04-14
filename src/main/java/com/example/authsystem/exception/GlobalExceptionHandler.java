// Package declaration for the exception handling class
package com.example.authsystem.exception;

// Import necessary Spring framework and Java classes
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// Marks this class as a centralized exception handling component
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles exceptions of type ProductNotFoundException
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException ex) {
        // Returns the exception message with a 404 (Not Found) HTTP status
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Handles any uncaught exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Sets the status code as 500 (Internal Server Error)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        // Returns a generic error message with the exception details
        return new ResponseEntity<>(
                "An unexpected error occurred: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
