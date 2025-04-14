package com.example.authsystem.dto;

import java.util.UUID;

public class PaystackInitializeRequestDTO {
    private UUID orderId;
    private String email;

    public PaystackInitializeRequestDTO() {}

    public PaystackInitializeRequestDTO(UUID orderId, String email) {
        this.orderId = orderId;
        this.email = email;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}