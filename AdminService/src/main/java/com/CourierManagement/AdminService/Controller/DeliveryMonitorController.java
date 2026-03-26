package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.DeliveryMonitorResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Service.DeliveryMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/deliveries")
@RequiredArgsConstructor
public class DeliveryMonitorController {

 private final DeliveryMonitorService service;

 
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

 // PUT /api/admin/deliveries/{deliveryId}/status
 // Called internally when Delivery Service pushes a status update
 @PutMapping("/{deliveryId}/status")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DeliveryMonitorResponse> updateStatus(
         @PathVariable String deliveryId,
         @RequestParam DeliveryStatus status) {
     return ResponseEntity.ok(service.updateStatus(deliveryId, status));
 }
}