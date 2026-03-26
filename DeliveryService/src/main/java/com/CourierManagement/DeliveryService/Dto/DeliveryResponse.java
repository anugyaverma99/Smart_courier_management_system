package com.CourierManagement.DeliveryService.Dto;

import java.time.LocalDateTime;

import com.CourierManagement.DeliveryService.Entity.DeliveryStatus;

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
public class DeliveryResponse {
 private Long id;
 private String trackingNumber;
 private String customerId;
 private AddressDto senderAddress;
 private AddressDto receiverAddress;
 private PackageDto packageDetails;
 private DeliveryStatus status;
 private double charge;
 private LocalDateTime pickupScheduledAt;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
}