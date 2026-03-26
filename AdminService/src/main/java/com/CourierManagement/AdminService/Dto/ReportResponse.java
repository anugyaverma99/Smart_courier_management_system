package com.CourierManagement.AdminService.Dto;


import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class ReportResponse {
 private Long id;
 private String reportType;
 private LocalDate fromDate;
 private LocalDate toDate;
 private int totalDeliveries;
 private int deliveredCount;
 private int failedCount;
 private int delayedCount;
 private int returnedCount;
 private String generatedBy;
 private LocalDateTime generatedAt;
 private int liveDeliveryCount;
 private int totalTrackingEvents;
}