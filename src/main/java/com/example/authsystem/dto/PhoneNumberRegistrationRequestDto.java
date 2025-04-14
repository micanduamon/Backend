// PhoneNumberRegistrationRequestDto.java
package com.example.authsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneNumberRegistrationRequestDto {
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format. Use international format (e.g., +15551234567)")
    private String phoneNumber;
}

