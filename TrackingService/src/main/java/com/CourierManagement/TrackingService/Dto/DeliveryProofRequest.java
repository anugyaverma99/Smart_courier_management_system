package com.CourierManagement.TrackingService.Dto;


import lombok.*;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DeliveryProofRequest {
	@NotBlank(message = "Delivery ID is required")
    private String deliveryId;

    @NotBlank(message = "Tracking number is required")
    private String trackingNumber;

    @NotBlank(message = "Received by is required")
    private String receivedBy;

    @NotBlank(message = "Submitted by is required")
    private String submittedBy;
 private String remarks;
 
 private LocalDateTime deliveredAt; 
}
