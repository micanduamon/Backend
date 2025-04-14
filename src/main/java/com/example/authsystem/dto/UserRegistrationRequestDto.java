package com.example.authsystem.dto;

import com.example.authsystem.model.User;
import lombok.Data;

@Data
public class UserRegistrationRequestDto {

    private String email;
    private String firstName;
    private String lastName;
    private String password;  // Plain text password from the request
    private User.AuthMethod methodType;
    private String biometricToken; //Optional biometric token

}
