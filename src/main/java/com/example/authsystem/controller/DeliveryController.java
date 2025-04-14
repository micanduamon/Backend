package com.example.authsystem.controller;

import com.example.authsystem.dto.DeliveryResponseDTO;
import com.example.authsystem.model.Delivery;
import com.example.authsystem.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    // POST Mapping removed, as Delivery creation logic is now managed within OrderService
    /*@PostMapping
    public ResponseEntity<DeliveryResponseDTO> createDelivery(@RequestBody DeliveryRequestDTO deliveryRequestDTO) {
        DeliveryResponseDTO createdDelivery = deliveryService.createDelivery(deliveryRequestDTO);
        return new ResponseEntity<>(createdDelivery, HttpStatus.CREATED);
    }*/

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryResponseDTO> updateDeliveryStatus(
            @PathVariable UUID id,
            @RequestParam("status") Delivery.DeliveryStatus status) {
        DeliveryResponseDTO updatedDelivery = deliveryService.updateDeliveryStatus(id, status);
        return ResponseEntity.ok(updatedDelivery);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDTO> getDelivery(@PathVariable UUID id) {
        DeliveryResponseDTO delivery = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(delivery);
    }
}