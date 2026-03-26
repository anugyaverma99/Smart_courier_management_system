package com.CourierManagement.DeliveryService.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CourierManagement.DeliveryService.Service.DeliveryService;

import jakarta.validation.Valid;

import java.util.List;
import com.CourierManagement.DeliveryService.Dto.CreateDeliveryRequest;
import com.CourierManagement.DeliveryService.Dto.DeliveryResponse;
import com.CourierManagement.DeliveryService.Dto.UpdateStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

 private final DeliveryService service;

 
 @PostMapping
 @PreAuthorize("hasRole('CUSTOMER')")
 public ResponseEntity<DeliveryResponse> createDelivery(
         @Valid @RequestBody CreateDeliveryRequest request) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.createDelivery(request));
 }


 @GetMapping("/my")
 @PreAuthorize("hasRole('CUSTOMER')")
 public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(
          @RequestParam String customerId) {
     return ResponseEntity.ok(service.getMyDeliveries(customerId));
 }

 @GetMapping("/{id}")
 @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
 public ResponseEntity<DeliveryResponse> getById(
         @PathVariable Long id) {
     return ResponseEntity.ok(service.getById(id));
 }


 @GetMapping("/track/{trackingNumber}")
 @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
 public ResponseEntity<DeliveryResponse> getByTrackingNumber(
         @PathVariable String trackingNumber) {
     return ResponseEntity.ok(service.getByTrackingNumber(trackingNumber));
 }

 @PutMapping("/{id}/status")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DeliveryResponse> updateStatus(
         @PathVariable Long id,
         @Valid @RequestBody UpdateStatusRequest request) {
     return ResponseEntity.ok(service.updateStatus(id, request));
 }
}