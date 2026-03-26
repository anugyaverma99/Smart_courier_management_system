package com.CourierManagement.AdminService.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.CourierManagement.AdminService.Dto.TrackingDto;

import java.util.List;

@FeignClient(name = "tracking-service")
public interface TrackingClient {

    @GetMapping("/tracking/{trackingNumber}")
    TrackingDto getTrackingInfo(@PathVariable("trackingNumber") String trackingNumber);

    @GetMapping("/tracking/delivery/{deliveryId}")
    List<TrackingDto> getEventsByDelivery(@PathVariable("deliveryId") Long deliveryId);
    
    @GetMapping("/tracking/count")
    long getTotalEventCount();
    @GetMapping("/tracking/{trackingNumber}/latest")
    TrackingDto getLatestStatus(@PathVariable("trackingNumber") String trackingNumber);
    
    @PostMapping("/tracking/event")
    void addTrackingEvent(
        @RequestParam String trackingNumber,
        @RequestParam String status,
        @RequestParam String remarks,
        @RequestParam String updatedBy);
}