package com.CourierManagement.AdminService.Dto;

import java.time.LocalDateTime;

import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class ExceptionResponse {
 private Long id;
 private String deliveryId;
 private String trackingNumber;
 private DeliveryStatus exceptionStatus;
 private ExceptionStatus resolutionStatus;
 private String reason;
 private String remarks;
 private String resolvedBy;
 private LocalDateTime raisedAt;
 private LocalDateTime resolvedAt;
}

