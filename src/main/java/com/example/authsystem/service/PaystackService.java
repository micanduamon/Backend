package com.example.authsystem.service;

import com.example.authsystem.dto.PaystackInitializeRequestDTO;
import com.example.authsystem.model.Orders;
import com.example.authsystem.model.Payment;
import com.example.authsystem.repository.OrderRepository;
import com.example.authsystem.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PaystackService {

    private static final Logger logger = Logger.getLogger(PaystackService.class.getName());

    @Value("${paystack.secretKey}")
    private String secretKey;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaystackService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Map<String, String> initializePayment(PaystackInitializeRequestDTO requestDTO) throws Exception {
        UUID orderId = requestDTO.getOrderId();
        String email = requestDTO.getEmail();

        Optional<Orders> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        Orders order = orderOptional.get();
        double amount = order.getOrderTotal() * 100; // Paystack expects amount in kobo

        String reference = generateReference();

        // Create and save a payment entry
        Payment payment = new Payment();
        payment.setOrder(order);  // ✅ Corrected from setOrderId(orderId)
        payment.setAmount(order.getOrderTotal());
        payment.setPaymentMethod("PAYSTACK");
        payment.setPaymentStatus("PENDING");
        payment.setPaystackReference(reference);
        paymentRepository.save(payment);

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", (int) amount);
        requestBody.put("email", email);
        requestBody.put("reference", reference);
        requestBody.put("callback_url", "http://localhost:8080/api/payments/paystack/verify/" + orderId);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(requestBody);

        // Send request to Paystack API
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.paystack.co/transaction/initialize");

        httpPost.setHeader("Authorization", "Bearer " + secretKey);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(requestJson, StandardCharsets.UTF_8));

        HttpResponse response = httpClient.execute(httpPost);
        int statusCode = response.getStatusLine().getStatusCode();
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        logger.info("Paystack API Response: " + responseBody);

        JsonNode rootNode = objectMapper.readTree(responseBody);

        if (statusCode >= 200 && statusCode < 300 && rootNode.get("status").asBoolean()) {
            Map<String, String> result = new HashMap<>();
            result.put("link", rootNode.get("data").get("authorization_url").asText());
            result.put("message", rootNode.get("message").asText());
            return result;
        } else {
            throw new RuntimeException("Paystack payment initialization failed: " + responseBody);
        }
    }

    public boolean verifyPayment(String reference) throws Exception {
        HttpClient httpClient = HttpClients.createDefault();
        String verifyUrl = "https://api.paystack.co/transaction/verify/" + reference;
        HttpGet httpGet = new HttpGet(verifyUrl);
        httpGet.setHeader("Authorization", "Bearer " + secretKey);

        HttpResponse response = httpClient.execute(httpGet);
        String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

        logger.info("Paystack Verify API Response: " + responseBody);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        return rootNode.get("status").asBoolean() && rootNode.get("data").get("status").asText().equals("success");
    }

    @Transactional
    public String handleSuccessfulVerification(UUID orderId, String reference) {
        try {
            Optional<Orders> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isEmpty()) {
                return "Order not found";
            }

            Orders order = orderOptional.get();

            // Verify the payment using Paystack API
            boolean verificationResponse = verifyPayment(reference);

            if (verificationResponse) {
                order.setStatus("COMPLETED");
                orderRepository.save(order);

                Optional<Payment> paymentOptional = paymentRepository.findByOrderId(orderId).stream().findFirst();
                if (paymentOptional.isPresent()) {
                    Payment payment = paymentOptional.get();
                    payment.setPaymentStatus("COMPLETED");
                    paymentRepository.save(payment);
                } else {
                    return "Payment record not found for order";
                }

                return "Payment verification successful";
            } else {
                logger.warning("Paystack payment verification failed for reference: " + reference);
                return "Payment verification failed";
            }
        } catch (Exception e) {
            logger.severe("Error during payment verification: " + e.getMessage());
            return "Error during payment verification";
        }
    }

    public String generateReference() {
        return UUID.randomUUID().toString();
    }
}
