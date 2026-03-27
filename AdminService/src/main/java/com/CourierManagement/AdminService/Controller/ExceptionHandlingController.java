package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.ExceptionResolveRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResponse;
import com.CourierManagement.AdminService.Service.ExceptionHandlingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/exceptions")
@RequiredArgsConstructor
@Tag(name = "Exception Handling", description = "Delivery exception management APIs")
public class ExceptionHandlingController {

    private final ExceptionHandlingService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get open exceptions", description = "Returns all unresolved delivery exceptions")
    public ResponseEntity<List<ExceptionResponse>> getOpenExceptions() {
        return ResponseEntity.ok(service.getOpenExceptions());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all exceptions", description = "Returns all exceptions including resolved ones")
    public ResponseEntity<List<ExceptionResponse>> getAllExceptions() {
        return ResponseEntity.ok(service.getAllExceptions());
    }

    @GetMapping("/delivery/{deliveryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get by delivery", description = "Returns all exceptions for a specific delivery")
    public ResponseEntity<List<ExceptionResponse>> getByDeliveryId(
            @PathVariable String deliveryId) {
        return ResponseEntity.ok(service.getByDeliveryId(deliveryId));
    }

    @PutMapping("/{exceptionId}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resolve exception", description = "Admin resolves a delivery exception")
    public ResponseEntity<ExceptionResponse> resolveException(
            @PathVariable Long exceptionId,
            @Valid @RequestBody ExceptionResolveRequest request) {
        return ResponseEntity.ok(service.resolveException(exceptionId, request));
    }
}