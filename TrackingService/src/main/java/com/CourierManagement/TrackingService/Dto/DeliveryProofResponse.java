package com.CourierManagement.TrackingService.Dto;



import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DeliveryProofResponse {
 private Long id;
 private String deliveryId;
 private String trackingNumber;
 private String receivedBy;
 private String proofImagePath;
 private String remarks;
 private String submittedBy;
 private LocalDateTime deliveredAt;
 private LocalDateTime createdAt;
}

