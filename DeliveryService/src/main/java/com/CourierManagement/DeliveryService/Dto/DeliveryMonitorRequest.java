package com.CourierManagement.DeliveryService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Used to notify Admin Service about new delivery or status change
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryMonitorRequest {

    private String deliveryId;        // DeliveryService DB ID
    private String trackingNumber;     // Generated tracking number
    private String customerName;       // Customer or sender name
    private String senderCity;
    private String receiverCity;
    private String currentStatus;      // DeliveryStatus as string
    private String assignedHub;        // default or calculated
}