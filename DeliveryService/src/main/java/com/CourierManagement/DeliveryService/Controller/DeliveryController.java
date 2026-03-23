package com.CourierManagement.DeliveryService.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CourierManagement.DeliveryService.Entity.Delivery;
import com.CourierManagement.DeliveryService.Entity.Status;
import com.CourierManagement.DeliveryService.Service.DeliveryService;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    // Create delivery
    @PostMapping
    public ResponseEntity<Delivery> createDelivery(@RequestBody Delivery delivery) {
        return ResponseEntity.ok(service.createDelivery(delivery));
    }

    // Get delivery by ID
    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDelivery(id));
    }

    // Get all deliveries for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Delivery>> getCustomerDeliveries(@PathVariable Long customerId) {
        return ResponseEntity.ok(service.getDeliveriesByCustomer(customerId));
    }

    // Update delivery status
    @PutMapping("/{id}/status")
    public ResponseEntity<Delivery> updateStatus(@PathVariable Long id,
                                                 @RequestParam Status status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}