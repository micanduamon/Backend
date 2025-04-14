package com.example.authsystem.service;

import com.example.authsystem.dto.PaymentRequestDTO;
import com.example.authsystem.dto.PaystackInitializeRequestDTO;
import com.example.authsystem.model.Payment;
import com.example.authsystem.model.Orders;
import com.example.authsystem.repository.OrderRepository;
import com.example.authsystem.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@EnableScheduling
public class PaymentService {

    private static final Logger logger = Logger.getLogger(PaymentService.class.getName());

    @Value("${paystack.secretKey}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    private final PaystackService paystackService;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, PaystackService paystackService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paystackService = paystackService;
    }

    // Process a payment
    @Transactional
    public Payment processPayment(PaymentRequestDTO paymentRequestDTO) {
        // Fetch the order
        Optional<Orders> orderOptional = orderRepository.findById(paymentRequestDTO.getOrderId());
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + paymentRequestDTO.getOrderId());
        }

        // Create a new Payment and set properties
        Payment payment = new Payment();
        payment.setOrder(orderOptional.get());
        payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("PENDING"); // Set initial status, you can update later
        payment.setPaystackReference(generateReference()); //add the Paystack reference

        // Save payment to the database
        return paymentRepository.save(payment);
    }
    public String generateReference() {
        return UUID.randomUUID().toString();
    }

    // Retrieve payment by ID
    public Optional<Payment> getPaymentById(UUID id) {  // Use UUID instead of String
        return paymentRepository.findById(id);
    }

    // Retrieve all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Update payment
    @Transactional
    public Payment updatePayment(UUID id, PaymentRequestDTO paymentRequestDTO) {  // Use UUID
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + id);
        }

        Payment payment = paymentOptional.get();
        payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        payment.setAmount(paymentRequestDTO.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentStatus("COMPLETED"); // Update status or any other changes

        return paymentRepository.save(payment);
    }

    // Delete payment
    @Transactional
    public void deletePayment(UUID id) {  // Use UUID
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()) {
            throw new RuntimeException("Payment not found with ID: " + id);
        }

        paymentRepository.delete(paymentOptional.get());
    }

    public boolean isValidPaystackSignature(String signature, String requestBody) throws IOException {
        try {
            Mac shaMac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            shaMac.init(keySpec);
            byte[] macData = shaMac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = new String(Hex.encodeHex(macData));

            // Compare signatures (case-insensitive)
            return expectedSignature.equalsIgnoreCase(signature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.severe("Error while calculating HMAC: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public void processPaystackWebhookEvent(String requestBody) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode event = mapper.readTree(requestBody);
        String eventType = event.get("event").asText();

        if ("charge.success".equals(eventType)) {
            JsonNode data = event.get("data");
            String reference = data.get("reference").asText();
            // Extract data from the "data" node like amount, customer details, etc.
            logger.info("Processing charge.success event for transaction reference: " + reference);

            // You'll need to implement logic to fetch order by reference, update the DB
            // based on the paystack data, and then send your delivery notification
            Optional<Payment> paymentOptional = paymentRepository.findByPaystackReference(reference).stream().findFirst();
            if(paymentOptional.isPresent()) {
                Payment payment = paymentOptional.get();
                payment.setPaymentStatus("COMPLETED");
                paymentRepository.save(payment);

                Optional<Orders> orderOptional = orderRepository.findById(payment.getOrder().getId());
                if (orderOptional.isPresent()) {
                    Orders order = orderOptional.get();
                    order.setStatus("COMPLETED");
                    orderRepository.save(order);
                }
                else {
                    logger.severe("Unable to find order " + reference);
                }

            }
            else {
                logger.severe("Unable to find Payment Optional for order with id of " + reference);
            }

        } else {
            logger.info("Received Paystack webhook event: " + eventType);
            // Handle other events (e.g., charge.failed, transfer.success, etc.)
        }
    }

    //Scheduled method to start a polling at a fixed rate of 10 seconds
    @Scheduled(fixedRate = 10000)
    public void checkPaymentStatuses() {

        //List of pending payments
        final List<Payment> payments = paymentRepository.findByPaymentStatus("PENDING");

        //Iterate over each payment in the payments list
        for(Payment payment : payments) {

            //Find the order id of the payment
            Optional<Orders> orderOptional = orderRepository.findById(payment.getOrder().getId());

            //if the ID is empty, then keep going on
            if (orderOptional.isEmpty()) {
                continue;
            }
            //Get the order that has the id
            Orders order = orderOptional.get();

            try {
                //Verify the payment
                boolean verificationResponse = paystackService.verifyPayment(payment.getPaystackReference());

                //payment was validated
                if (verificationResponse) {
                    // Update the order status
                    order.setStatus("COMPLETED");
                    orderRepository.save(order);

                    //Also update to payment completed
                    payment.setPaymentStatus("COMPLETED");
                    paymentRepository.save(payment);

                    //To do the message - this will come in another message!
                } else {
                    //Payment verification failed, log the error
                    logger.warning("Paystack payment verification failed for reference: " + payment.getPaystackReference());
                }
            }
            catch (Exception e) {
                logger.severe("Error during payment verification: " + e.getMessage());
            }
        }
    }
}