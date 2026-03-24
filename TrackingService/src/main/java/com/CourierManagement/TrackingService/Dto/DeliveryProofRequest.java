package com.CourierManagement.TrackingService.Dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DeliveryProofRequest {
 private String deliveryId;
 private String trackingNumber;
 private String receivedBy;
 private String remarks;
 private String submittedBy;
 private LocalDateTime deliveredAt; 
}
