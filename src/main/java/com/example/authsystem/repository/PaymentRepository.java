package com.example.authsystem.repository;

import com.example.authsystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.Optional; // Import Optional


public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // Find payments by payment status
    List<Payment> findByPaymentStatus(String paymentStatus);

    // Find payments by order ID
    List<Payment> findByOrderId(UUID orderId);

    // Find payments by order ID and payment status
    List<Payment> findByOrderIdAndPaymentStatus(UUID orderId, String paymentStatus);

    // New method to find payment by Paystack reference
    Optional<Payment> findByPaystackReference(String paystackReference);  // Add this line

}
