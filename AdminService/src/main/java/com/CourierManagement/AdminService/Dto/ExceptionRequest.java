package com.CourierManagement.AdminService.Dto;

import com.CourierManagement.AdminService.Entity.DeliveryStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionRequest {
 private String deliveryId;
 private String trackingNumber;
 private DeliveryStatus exceptionStatus; // DELAYED / FAILED / RETURNED
 private String reason;
}