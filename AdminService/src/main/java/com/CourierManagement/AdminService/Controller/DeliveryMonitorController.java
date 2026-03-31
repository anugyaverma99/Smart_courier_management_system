package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.DeliveryMonitorRequest;
import com.CourierManagement.AdminService.Dto.DeliveryMonitorResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Service.DeliveryMonitorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/deliveries")
@RequiredArgsConstructor
public class DeliveryMonitorController {

 private final DeliveryMonitorService service;

@PostMapping("/sync")
public ResponseEntity<DeliveryMonitorResponse> syncDelivery(
      @RequestBody DeliveryMonitorRequest request) {
  return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(service.syncDelivery(request));
}
 
 @GetMapping
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<DeliveryMonitorResponse>> getAllDeliveries() {
     return ResponseEntity.ok(service.getAllDeliveries());
 }

 
 @GetMapping("/{deliveryId}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DeliveryMonitorResponse> getByDeliveryId(
         @PathVariable String deliveryId) {
     return ResponseEntity.ok(service.getByDeliveryId(deliveryId));
 }


 @GetMapping("/status/{status}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<DeliveryMonitorResponse>> getByStatus(
         @PathVariable DeliveryStatus status) {
     return ResponseEntity.ok(service.getByStatus(status));
 }

 
 @GetMapping("/hub/{hubName}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<DeliveryMonitorResponse>> getByHub(
         @PathVariable String hubName) {
     return ResponseEntity.ok(service.getByHub(hubName));
 }

 @PutMapping("/{deliveryId}/status")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DeliveryMonitorResponse> updateStatus(
         @PathVariable String deliveryId,
         @Valid @RequestParam String status) {  DeliveryStatus newStatus = DeliveryStatus.valueOf(status); // convert string to enum
         return ResponseEntity.ok(service.updateStatus(deliveryId, newStatus));
 }
}