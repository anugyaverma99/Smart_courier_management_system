package com.CourierManagement.AdminService.Controller;


import com.CourierManagement.AdminService.Dto.HubRequest;
import com.CourierManagement.AdminService.Dto.HubResponse;
import com.CourierManagement.AdminService.Service.HubService;

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
public class HubController {

 private final HubService service;

 
 @PostMapping
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<HubResponse> createHub(
         @Valid @RequestBody HubRequest request) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.createHub(request));
 }

 
 @GetMapping
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<HubResponse>> getActiveHubs() {
     return ResponseEntity.ok(service.getActiveHubs());
 }

 
 @GetMapping("/all")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<HubResponse>> getAllHubs() {
     return ResponseEntity.ok(service.getAllHubs());
 }

 
 @DeleteMapping("/{hubId}")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<HubResponse> deactivateHub(
         @PathVariable Long hubId) {
     return ResponseEntity.ok(service.deactivateHub(hubId));
 }
}