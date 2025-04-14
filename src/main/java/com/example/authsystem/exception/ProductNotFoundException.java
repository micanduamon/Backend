// Package declaration for the custom exception class
package com.example.authsystem.exception;

// Import necessary annotations and classes for exception handling
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Marks this class as an exception that will result in a 404 (Not Found) HTTP response
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends RuntimeException {

    // Constructor that accepts a custom error message
    public ProductNotFoundException(String message) {
        // Passes the error message to the parent RuntimeException class
        super(message);
    }
}
