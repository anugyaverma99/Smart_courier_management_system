package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.ExceptionResolveRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResponse;
import com.CourierManagement.AdminService.Service.ExceptionHandlingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/exceptions")
@RequiredArgsConstructor
public class ExceptionHandlingController {

 private final ExceptionHandlingService service;

 
 @GetMapping
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<ExceptionResponse>> getOpenExceptions() {
     return ResponseEntity.ok(service.getOpenExceptions());
 }

 
 @GetMapping("/all")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<ExceptionResponse>> getAllExceptions() {
     return ResponseEntity.ok(service.getAllExceptions());
 }

 
 @GetMapping("/delivery/{deliveryId}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<ExceptionResponse>> getByDeliveryId(
         @PathVariable String deliveryId) {
     return ResponseEntity.ok(service.getByDeliveryId(deliveryId));
 }

 
 @PutMapping("/{exceptionId}/resolve")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<ExceptionResponse> resolveException(
         @PathVariable Long exceptionId,
        @Valid @RequestBody ExceptionResolveRequest request) {
     return ResponseEntity.ok(service.resolveException(exceptionId, request));
 }
 
}