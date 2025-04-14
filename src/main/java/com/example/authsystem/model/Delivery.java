package com.example.authsystem.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Delivery {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    private String deliveryAddress; // Recipient's address

    private String trackingNumber;   // Optional: If using a third-party delivery service

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;    // Delivery status (e.g., CREATED, IN_TRANSIT, DELIVERED, FAILED)

    private LocalDateTime estimatedDeliveryDate;

    private LocalDateTime actualDeliveryDate;

    // Add fields for delivery driver information (optional)
    // private String deliveryDriverName;
    // private String deliveryDriverPhoneNumber;

    public enum DeliveryStatus {
        CREATED,
        IN_TRANSIT,
        DELIVERED,
        FAILED,
        CANCELLED, // Add a cancelled status
        PENDING
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(LocalDateTime actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }
}
