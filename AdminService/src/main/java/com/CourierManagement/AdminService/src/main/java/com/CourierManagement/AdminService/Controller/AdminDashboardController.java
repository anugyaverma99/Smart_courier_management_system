package com.CourierManagement.AdminService.Controller;


import com.CourierManagement.AdminService.Dto.DashboardResponse;
import com.CourierManagement.AdminService.Service.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Admin dashboard APIs")
public class AdminDashboardController {

 private final AdminDashboardService service;

 // get admin dashboard stats
 @GetMapping("/dashboard")
 @PreAuthorize("hasRole('ADMIN')")
 @Operation(summary = "Get dashboard", description = "Returns counts for deliveries, exceptions, hubs")
 public ResponseEntity<DashboardResponse> getDashboard() {
     return ResponseEntity.ok(service.getDashboard());
 }
}
