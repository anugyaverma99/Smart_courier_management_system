package com.CourierManagement.AdminService.Dto;



import lombok.*;

@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class DashboardResponse {
 private long totalDeliveries;
 private long deliveredToday;
 private long inTransit;
 private long outForDelivery;
 private long exceptions;      // DELAYED , FAILED , RETURNED
 private long activeHubs;
 private int liveDeliveryCount;       // live from Delivery Service
 private long totalTrackingEvents;
}