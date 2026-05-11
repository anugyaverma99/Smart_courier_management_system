package com.CourierManagement.AdminService.Dto;

import lombok.Data;
import lombok.Builder;
@Builder
@Data

public class DeliveryDto {
    private Long id;
    private String trackingNumber;
    private String status;
    private String senderName;
    private String receiverName;
    private java.util.Map<String, Object> receiverAddress;
    private String createdAt;
}
