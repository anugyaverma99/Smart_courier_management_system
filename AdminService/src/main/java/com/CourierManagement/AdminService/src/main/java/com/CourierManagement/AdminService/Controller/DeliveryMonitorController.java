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

// sync a delivery into the monitor table
@PostMapping("/sync")
public ResponseEntity<DeliveryMonitorResponse> syncDelivery(
      @RequestBody DeliveryMonitorRequest request) {
  return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(service.syncDelivery(request));
}
 
 // get all monitored deliveries
 @GetMapping
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<DeliveryMonitorResponse>> getAllDeliveries() {
     return ResponseEntity.ok(service.getAllDeliveries());
 }

 // get a single monitor entry by delivery id
 @GetMapping("/{deliveryId}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DeliveryMonitorResponse> getByDeliveryId(
         @PathVariable String deliveryId) {
     return ResponseEntity.ok(service.getByDeliveryId(deliveryId));
 }

 // filter deliveries by status
 @GetMapping("/status/{status}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<DeliveryMonitorResponse>> getByStatus(
         @PathVariable DeliveryStatus status) {
     return ResponseEntity.ok(service.getByStatus(status));
 }

 // filter deliveries by hub name
 @GetMapping("/hub/{hubName}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<DeliveryMonitorResponse>> getByHub(
         @PathVariable String hubName) {
     return ResponseEntity.ok(service.getByHub(hubName));
 }

 // update delivery status in monitor
 @PutMapping("/{deliveryId}/status")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DeliveryMonitorResponse> updateStatus(
         @PathVariable String deliveryId,
         @Valid @RequestParam String status) {  
	 
	 // convert string to enum
	 DeliveryStatus newStatus;
	 try {
	     newStatus = DeliveryStatus.valueOf(status.toUpperCase());
	 } catch (IllegalArgumentException e) {
	     return ResponseEntity.badRequest().build();
	 }
         return ResponseEntity.ok(service.updateStatus(deliveryId, newStatus));
 }
}