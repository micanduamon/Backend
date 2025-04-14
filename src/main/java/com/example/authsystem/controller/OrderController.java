package com.example.authsystem.controller;

import com.example.authsystem.dto.OrderRequestDTO;
import com.example.authsystem.model.Orders;
import com.example.authsystem.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        logger.info("Received request to create order for userId: {}", orderRequestDTO.getUserId());

        try {
            Orders createdOrder = orderService.createOrder(orderRequestDTO);
            logger.info("Order created successfully with ID: {}", createdOrder.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", "/api/orders/" + createdOrder.getId())
                    .body(createdOrder); // Return the created order in the body
        } catch (Exception e) {
            logger.error("Error occurred while creating order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating order"); // Return error message
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orders> getOrder(@PathVariable UUID id) {
        Optional<Orders> orderOptional = orderService.findById(id);
        if (orderOptional.isPresent()) {
            Orders order = orderOptional.get();
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}