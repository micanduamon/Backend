package com.example.authsystem.dto;

import lombok.Data;

@Data
public class UserResponseDto {

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

}
