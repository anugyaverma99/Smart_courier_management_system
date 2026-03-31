package com.CourierManagement.AdminService.Dto;

import com.CourierManagement.AdminService.Entity.DeliveryStatus;

import lombok.*;

@Getter
@Setter 
@NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryMonitorRequest {
    private String deliveryId;
    private String trackingNumber;
    private String customerName;
    private String senderCity;
    private String receiverCity;
    private DeliveryStatus currentStatus;
    private String assignedHub;
}
