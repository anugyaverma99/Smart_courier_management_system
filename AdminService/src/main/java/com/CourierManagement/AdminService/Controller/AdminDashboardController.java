package com.CourierManagement.AdminService.Controller;


import com.CourierManagement.AdminService.Dto.DashboardResponse;
import com.CourierManagement.AdminService.Service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

 private final AdminDashboardService service;

 
 @GetMapping("/dashboard")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<DashboardResponse> getDashboard() {
     return ResponseEntity.ok(service.getDashboard());
 }
}
