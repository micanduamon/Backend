package com.example.authsystem.service;

import com.example.authsystem.dto.DeliveryResponseDTO;
import com.example.authsystem.model.Delivery;
import com.example.authsystem.model.Orders;
import com.example.authsystem.repository.DeliveryRepository;
import com.example.authsystem.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
    }

    //Method deleted as it is not useful
   /* @Transactional
    public DeliveryResponseDTO createDelivery(DeliveryRequestDTO deliveryRequestDTO) {
        Orders order = orderRepository.findById(deliveryRequestDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + deliveryRequestDTO.getOrderId()));

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryAddress(deliveryRequestDTO.getDeliveryAddress());
        delivery.setStatus(Delivery.DeliveryStatus.CREATED);
        delivery.setEstimatedDeliveryDate(deliveryRequestDTO.getEstimatedDeliveryDate());

        Delivery savedDelivery = deliveryRepository.save(delivery);
        return convertToResponseDTO(savedDelivery);
    }*/

    @Transactional
    public DeliveryResponseDTO updateDeliveryStatus(UUID deliveryId, Delivery.DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));

        delivery.setStatus(newStatus);

        //Set TrackNumber
        if (newStatus == Delivery.DeliveryStatus.IN_TRANSIT && delivery.getTrackingNumber() == null) {
            delivery.setTrackingNumber(generateTrackingNumber());
        }

        //Set time
        if (newStatus == Delivery.DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryDate(java.time.LocalDateTime.now());
        }

        Delivery updatedDelivery = deliveryRepository.save(delivery);
        return convertToResponseDTO(updatedDelivery);
    }

    public DeliveryResponseDTO getDeliveryById(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found with ID: " + deliveryId));
        return convertToResponseDTO(delivery);
    }
    // Add more methods as needed (e.g., get deliveries by order ID, etc.)

    private DeliveryResponseDTO convertToResponseDTO(Delivery delivery) {
        DeliveryResponseDTO dto = new DeliveryResponseDTO();
        dto.setId(delivery.getId());
        dto.setOrderId(delivery.getOrder().getId());
        dto.setDeliveryAddress(delivery.getDeliveryAddress());
        dto.setTrackingNumber(delivery.getTrackingNumber());
        dto.setStatus(delivery.getStatus());
        dto.setEstimatedDeliveryDate(delivery.getEstimatedDeliveryDate());
        dto.setActualDeliveryDate(delivery.getActualDeliveryDate());
        return dto;
    }

    private String generateTrackingNumber() {
        return UUID.randomUUID().toString();
    }
}