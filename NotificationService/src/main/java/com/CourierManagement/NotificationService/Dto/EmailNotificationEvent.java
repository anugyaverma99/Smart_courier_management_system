package com.CourierManagement.NotificationService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationEvent implements Serializable {
    private String trackingNumber;
    private String status;
    private String customerEmail;
    private String customerName;
    private String remarks;
}
