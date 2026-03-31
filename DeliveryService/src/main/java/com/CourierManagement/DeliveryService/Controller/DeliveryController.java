package com.CourierManagement.DeliveryService.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.CourierManagement.DeliveryService.Service.DeliveryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Delivery", description = "Delivery management APIs")
public class DeliveryController {

 private final DeliveryService service;

 @PostMapping
 @PreAuthorize("hasRole('CUSTOMER')")
 @Operation(summary = "Create delivery", description = "Customer creates a new delivery request")
 public ResponseEntity<DeliveryResponse> createDelivery(
         @Valid @RequestBody CreateDeliveryRequest request) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.createDelivery(request));
 }
  @GetMapping("/my")
 @PreAuthorize("hasRole('CUSTOMER')")
 @Operation(summary = "Get my deliveries", description = "Get all deliveries for a customer")
  public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(
          @RequestParam String customerId) {
	  System.out.println("Customer ID received: " + customerId);
     return ResponseEntity.ok(service.getMyDeliveries(customerId));
 }

 @GetMapping("/{id}")
 @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
 @Operation(summary = "Get delivery by ID", description = "Get delivery details")
 
 public ResponseEntity<DeliveryResponse> getById(
         @PathVariable Long id) {
     return ResponseEntity.ok(service.getById(id));
 }


 @GetMapping("/track/{trackingNumber}")
 @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
 @Operation(summary = "Track delivery", description = "Get delivery by tracking number")
 public ResponseEntity<DeliveryResponse> getByTrackingNumber(
         @PathVariable String trackingNumber) {
     return ResponseEntity.ok(service.getByTrackingNumber(trackingNumber));
 }

 @PutMapping("/{id}/status")
 @PreAuthorize("hasRole('ADMIN')")
 @Operation(summary = "Update status", description = "Admin updates delivery lifecycle status")
 public ResponseEntity<DeliveryResponse> updateStatus(
         @PathVariable Long id,
         @Valid @RequestBody UpdateStatusRequest request) {
     return ResponseEntity.ok(service.updateStatus(id, request));
 }

@GetMapping("/{id}/exists")
public ResponseEntity<Boolean> doesDeliveryExist(
      @PathVariable String id) {
  try {
      service.getById(Long.parseLong(id));
      return ResponseEntity.ok(true);
  } catch (Exception e) {
      return ResponseEntity.ok(false);
  }
}

@GetMapping
@PreAuthorize("hasRole('ADMIN')")
@Operation(summary = "Get all deliveries", description = "Admin gets all deliveries")
public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
 return ResponseEntity.ok(service.getAllDeliveries());
}
}