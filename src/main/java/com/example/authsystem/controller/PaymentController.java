package com.example.authsystem.controller;

import com.example.authsystem.dto.PaymentRequestDTO;
import com.example.authsystem.dto.PaystackInitializeRequestDTO;
import com.example.authsystem.model.Payment;
import com.example.authsystem.service.PaystackService;
import com.example.authsystem.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaystackService paystackService;

    public PaymentController(PaymentService paymentService, PaystackService paystackService) {
        this.paymentService = paymentService;
        this.paystackService = paystackService;
    }

    // POST method to process payment
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            Payment payment = paymentService.processPayment(paymentRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/paystack/initialize") // Changed to POST
    public ResponseEntity<?> initializePayment(@RequestBody PaystackInitializeRequestDTO requestDTO) { // Changed parameter
        try {
            Map<String, String> paymentLinkResponse = paystackService.initializePayment(requestDTO);
            return ResponseEntity.ok(paymentLinkResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error initializing payment: " + e.getMessage());
        }
    }

    @GetMapping("/paystack/verify/{orderId}")
    public ResponseEntity<?> verifyPayment(
            @PathVariable UUID orderId,
            @RequestParam("trxref") String reference // Paystack sends the reference as "trxref"
    ) {
        String result = paystackService.handleSuccessfulVerification(orderId, reference);
        if (result.equals("Payment verification successful")) {
            return ResponseEntity.ok("Payment verification successful");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed: " + result);
        }
    }

    // GET method to retrieve a specific payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable("id") String id) {
        try {
            // Convert string to UUID
            UUID paymentId = UUID.fromString(id);
            Optional<Payment> payment = paymentService.getPaymentById(paymentId);
            if (payment.isPresent()) {
                return ResponseEntity.ok(payment.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found for ID: " + id);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        }
    }

    // GET method to retrieve all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // PUT method to update a payment
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable("id") String id, @RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            // Convert string to UUID
            UUID paymentId = UUID.fromString(id);
            Payment updatedPayment = paymentService.updatePayment(paymentId, paymentRequestDTO);
            return ResponseEntity.ok(updatedPayment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    // DELETE method to remove a payment by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable("id") String id) {
        try {
            // Convert string to UUID
            UUID paymentId = UUID.fromString(id);
            paymentService.deletePayment(paymentId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Payment deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid UUID format");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}