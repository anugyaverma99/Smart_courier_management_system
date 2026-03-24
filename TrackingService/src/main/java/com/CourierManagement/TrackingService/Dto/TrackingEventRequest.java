package com.CourierManagement.TrackingService.Dto;


import com.CourierManagement.TrackingService.Entity.TrackingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class TrackingEventRequest {

 private String deliveryId;       
 private String trackingNumber; 
 private TrackingStatus status; 
 private String location;
 private String remarks;
 private String updatedBy;

}