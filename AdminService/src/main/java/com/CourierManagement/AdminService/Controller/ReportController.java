package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.ReportResponse;
import com.CourierManagement.AdminService.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class ReportController {

 private final ReportService service;

 @PostMapping("/generate")
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<ReportResponse> generateReport(
         @RequestParam String reportType,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
         @RequestParam String generatedBy) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.generateReport(reportType, fromDate, toDate, generatedBy));
 }


 @GetMapping
 @PreAuthorize("hasRole('ADMIN')")
 public ResponseEntity<List<ReportResponse>> getReports(
         @RequestParam String reportType) {
     return ResponseEntity.ok(service.getReports(reportType));
 }
}
