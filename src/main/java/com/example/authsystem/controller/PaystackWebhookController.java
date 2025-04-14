package com.example.authsystem.controller;

import com.example.authsystem.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

@RestController
@RequestMapping("/api/payments/paystack")
public class PaystackWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(PaystackWebhookController.class);

    private final PaymentService paymentService;

    public PaystackWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        logger.info("Received Paystack webhook request");

        // Verify Paystack signature (important for security)
        String signature = request.getHeader("x-paystack-signature");
        if (!paymentService.isValidPaystackSignature(signature, requestBody)) {
            logger.warn("Invalid Paystack signature received!");
            return new ResponseEntity<>("Invalid Paystack signature", HttpStatus.BAD_REQUEST);
        }

        try {
            // Process the webhook event
            paymentService.processPaystackWebhookEvent(requestBody);
            return new ResponseEntity<>(HttpStatus.OK); // Acknowledge receipt
        } catch (Exception e) {
            logger.error("Error processing Paystack webhook event: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Indicate failure
        }
    }
}