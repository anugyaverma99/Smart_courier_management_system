package com.CourierManagement.AdminService.Service;


import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Client.TrackingClient;
import com.CourierManagement.AdminService.Dto.DashboardResponse;
import com.CourierManagement.AdminService.Dto.DeliveryDto;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;
import com.CourierManagement.AdminService.Repository.DeliveryExceptionRepository;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import com.CourierManagement.AdminService.Repository.HubRepository;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

 private final DeliveryMonitorRepository deliveryMonitorRepository;
 private final DeliveryExceptionRepository exceptionRepository;
 private final HubRepository hubRepository;
 private final DeliveryClient deliveryClient;    
 private final TrackingClient trackingClient;

 public DashboardResponse getDashboard() {
	 List<DeliveryDto> allDeliveries = deliveryClient.getAllDeliveries();

     long totalTrackingEvents = trackingClient.getTotalEventCount();
     return DashboardResponse.builder()
             .totalDeliveries(
                     deliveryMonitorRepository.count())
             .deliveredToday(
                     deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.DELIVERED))
             .inTransit(
                     deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.IN_TRANSIT))
             .outForDelivery(
                     deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.OUT_FOR_DELIVERY))
             .exceptions(
                     exceptionRepository.countByResolutionStatus(ExceptionStatus.OPEN))
             .activeHubs(
                     hubRepository.countByActiveTrue())
             .liveDeliveryCount(allDeliveries.size())
             .totalTrackingEvents(totalTrackingEvents)
             .build();
 }
}