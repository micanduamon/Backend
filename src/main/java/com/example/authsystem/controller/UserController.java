package com.example.authsystem.controller;

import com.example.authsystem.dto.PhoneNumberRegistrationRequestDto;
import com.example.authsystem.dto.VerificationRequestDto;
import com.example.authsystem.dto.UserLoginRequestDto;
import com.example.authsystem.dto.UserRegistrationRequestDto;
import com.example.authsystem.dto.UserResponseDto;
import com.example.authsystem.model.User;
import com.example.authsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.authsystem.exception.AuthenticationException;
import org.slf4j.Logger; // Correct import statement
import org.slf4j.LoggerFactory;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationRequestDto registrationDto) {
        User registeredUser = userService.registerUser(registrationDto);
        UserResponseDto responseDto = mapToUserResponseDto(registeredUser);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login/email")
    public ResponseEntity<?> loginWithEmailPassword(@RequestBody UserLoginRequestDto loginDto) {
        try {
            String jwtToken = userService.loginWithEmailPassword(loginDto);
            return ResponseEntity.ok(jwtToken); // Return the JWT token in the response
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage()); // Return 401 for authentication failure
        }
    }

    @PostMapping("/login/biometric")
    public ResponseEntity<UserResponseDto> loginWithBiometric(@RequestParam String biometricToken) {
        Optional<User> user = userService.loginWithBiometric(biometricToken);
        return user.map(u -> ResponseEntity.ok(mapToUserResponseDto(u)))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/register/phone")
    public ResponseEntity<?> registerPhoneNumber(@Valid @RequestBody PhoneNumberRegistrationRequestDto request) {
        try {
            userService.registerPhoneNumber(request.getPhoneNumber());
            return new ResponseEntity<>(HttpStatus.OK); // Or a more specific message
        } catch (Exception e) {
            logger.error("Error registering phone number: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify/phone")
    public ResponseEntity<?> verifyPhoneNumber(@Valid @RequestBody VerificationRequestDto request) {
        try {
            String jwtToken = userService.verifyPhoneNumber(request.getPhoneNumber(), request.getVerificationCode());
            return new ResponseEntity<>(jwtToken, HttpStatus.OK); // Return the JWT
        } catch (AuthenticationException e) {
            logger.error("Error verifying phone number: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED); // Or Forbidden, depending on the situation
        }
    }

    // Helper method to map User to UserResponseDto
    private UserResponseDto mapToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        return dto;
    }
}