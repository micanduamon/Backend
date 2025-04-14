package com.example.authsystem.service;

import com.example.authsystem.dto.OrderRequestDTO;
import com.example.authsystem.model.Orders;
import com.example.authsystem.model.OrderItem;
import com.example.authsystem.model.Delivery; // Import the Delivery entity
import com.example.authsystem.repository.OrderRepository;
import com.example.authsystem.repository.DeliveryRepository; // Import the DeliveryRepository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository; // Inject the DeliveryRepository

    public OrderService(OrderRepository orderRepository, DeliveryRepository deliveryRepository) {
        this.orderRepository = orderRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional // Ensures that both order and delivery are saved or rolled back
    public Orders createOrder(OrderRequestDTO orderRequestDTO) {
        // Creating a new order object
        Orders order = new Orders();
        order.setUserId(orderRequestDTO.getUserId());
        order.setStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        // Mapping order items from DTO to entity
        order.setOrderItems(
                orderRequestDTO.getOrderItems().stream().map(itemDTO -> {
                    OrderItem item = new OrderItem();
                    item.setProductId(itemDTO.getProductId());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setPrice(itemDTO.getPrice());
                    item.setOrder(order); // Associate order with order item
                    return item;
                }).collect(Collectors.toList())
        );

        // Calculating total order price
        double orderTotal = order.getOrderItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        order.setOrderTotal(orderTotal);

        // Saving the order BEFORE creating the delivery
        Orders savedOrder = orderRepository.save(order);

        // Create and save the Delivery object
        Delivery delivery = new Delivery();
        delivery.setOrder(savedOrder);  // Associate the delivery with the order
        delivery.setDeliveryAddress("TBD"); // Set an initial delivery address
        delivery.setStatus(Delivery.DeliveryStatus.PENDING); // Initial delivery status
        Delivery savedDelivery = deliveryRepository.save(delivery);

        //Associate delivery to order
        savedOrder.setDelivery(delivery);
        orderRepository.save(savedOrder);

        return savedOrder;
    }

    // Find order by ID
    public Optional<Orders> findById(UUID id) {
        return orderRepository.findById(id);
    }
}