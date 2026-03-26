package com.CourierManagement.AdminService.Dto;


import lombok.*;
import java.time.LocalDateTime;

import com.CourierManagement.AdminService.Entity.DeliveryStatus;

@Getter
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMonitorResponse {
 private Long id;
 private String deliveryId;
 private String trackingNumber;
 private String customerName;
 private String senderCity;
 private String receiverCity;
 private DeliveryStatus currentStatus;
 private String assignedHub;
 private LocalDateTime lastUpdated;
 private String liveSenderName;
 private String liveReceiverName;
 private String latestTrackingStatus;
 private String latestTrackingLocation;
}