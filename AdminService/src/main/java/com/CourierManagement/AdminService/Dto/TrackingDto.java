package com.CourierManagement.AdminService.Dto;


import lombok.Data;

@Data
public class TrackingDto {
    private Long id;
    private String trackingNumber;
    private String status;
    private String location;
    private String updatedAt;
}
