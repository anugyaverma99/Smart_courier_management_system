package com.CourierManagement.AdminService.Service;

import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Client.TrackingClient;
import com.CourierManagement.AdminService.Dto.DashboardResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;
import com.CourierManagement.AdminService.Repository.DeliveryExceptionRepository;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import com.CourierManagement.AdminService.Repository.HubRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final DeliveryMonitorRepository deliveryMonitorRepository;
    private final DeliveryExceptionRepository exceptionRepository;
    private final HubRepository hubRepository;
    private final DeliveryClient deliveryClient;
    private final TrackingClient trackingClient;

    public DashboardResponse getDashboard() {

        int liveDeliveryCount = 0;
        long totalTrackingEvents = 0;

        try {
            liveDeliveryCount = deliveryClient.getAllDeliveries().size();
        } catch (Exception e) {
            log.warn("Could not fetch live delivery count: {}", e.getMessage());
        }

        try {
            totalTrackingEvents = trackingClient.getTotalEventCount();
        } catch (Exception e) {
            log.warn("Could not fetch tracking event count: {}", e.getMessage());
        }

        return DashboardResponse.builder()
                .totalDeliveries(deliveryMonitorRepository.count())
                .deliveredToday(deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.DELIVERED))
                .inTransit(deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.IN_TRANSIT))
                .outForDelivery(deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.OUT_FOR_DELIVERY))
                .exceptions(exceptionRepository
                        .countByResolutionStatus(ExceptionStatus.OPEN))
                .activeHubs(hubRepository.countByActiveTrue())
                .liveDeliveryCount(liveDeliveryCount)
                .totalTrackingEvents(totalTrackingEvents)
                .build();
    }
}