package com.CourierManagement.AdminService.Controller;


import com.CourierManagement.AdminService.Dto.HubRequest;
import com.CourierManagement.AdminService.Dto.HubResponse;
import com.CourierManagement.AdminService.Service.HubService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/hubs")
@RequiredArgsConstructor
@Tag(name = "Hub Management", description = "Hub CRUD APIs")
public class HubController {

 private final HubService service;

 
 @PostMapping
 @PreAuthorize("hasRole('ADMIN')")
 @Operation(summary = "Create hub", description = "Admin creates a new delivery hub")
 public ResponseEntity<HubResponse> createHub(
         @Valid @RequestBody HubRequest request) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.createHub(request));
 }

 
 @GetMapping
 @PreAuthorize("hasRole('ADMIN')")
 @Operation(summary = "Get active hubs", description = "List all active hubs")
 
 public ResponseEntity<List<HubResponse>> getActiveHubs() {
     return ResponseEntity.ok(service.getActiveHubs());
 }

 
 @GetMapping("/all")
 @PreAuthorize("hasRole('ADMIN')")
 @Operation(summary = "Get all hubs", description = "List all hubs")
 
 public ResponseEntity<List<HubResponse>> getAllHubs() {
     return ResponseEntity.ok(service.getAllHubs());
 }

 
 @DeleteMapping("/{hubId}")
 @PreAuthorize("hasRole('ADMIN')")
 @Operation(summary = "Deactivate hub", description = "Soft delete — sets active = false")
 public ResponseEntity<HubResponse> deactivateHub(
         @PathVariable Long hubId) {
     return ResponseEntity.ok(service.deactivateHub(hubId));
 }
}