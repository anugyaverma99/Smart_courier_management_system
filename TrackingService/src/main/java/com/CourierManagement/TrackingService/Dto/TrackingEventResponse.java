package com.CourierManagement.TrackingService.Dto;


import lombok.*;
import java.time.LocalDateTime;

import com.CourierManagement.TrackingService.Entity.TrackingStatus;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class TrackingEventResponse {

 private Long id;
 private String deliveryId;
 private String trackingNumber;
 private TrackingStatus status;
 private String location;
 private String remarks;
 private String updatedBy;
 private LocalDateTime eventTime;
 private LocalDateTime createdAt;
}
