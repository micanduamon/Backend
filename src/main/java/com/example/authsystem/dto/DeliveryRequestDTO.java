package com.example.authsystem.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DeliveryRequestDTO {
    private UUID orderId;
    private String deliveryAddress;
    private LocalDateTime estimatedDeliveryDate;

    //add delivery driver name & delivery driver phone number if needed

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
}
