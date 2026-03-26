package com.CourierManagement.AdminService.Dto;



import lombok.Data;

@Data
public class DeliveryDto {
    private Long id;
    private String trackingNumber;
    private String status;
    private String senderName;
    private String receiverName;
    private String createdAt;
}
