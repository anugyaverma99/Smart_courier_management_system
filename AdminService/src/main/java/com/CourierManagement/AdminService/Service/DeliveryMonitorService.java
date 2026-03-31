package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Client.TrackingClient;
import com.CourierManagement.AdminService.Dto.DeliveryDto;
import com.CourierManagement.AdminService.Dto.DeliveryMonitorRequest;
import com.CourierManagement.AdminService.Dto.DeliveryMonitorResponse;
import com.CourierManagement.AdminService.Dto.TrackingDto;
import com.CourierManagement.AdminService.Entity.DeliveryMonitor;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryMonitorService {

 private final DeliveryMonitorRepository repository;
 private final DeliveryClient deliveryClient;    
 private final TrackingClient trackingClient;


 public List<DeliveryMonitorResponse> getAllDeliveries() {
     return repository.findAll()
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }


 public List<DeliveryMonitorResponse> getByStatus(DeliveryStatus status) {
     return repository.findByCurrentStatus(status)
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }

 // Filter by hub — admin wants to see parcels at a specific hub
 public List<DeliveryMonitorResponse> getByHub(String hubName) {
     return repository.findByAssignedHub(hubName)
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }
 
 // Single delivery detail
//Single delivery detail — enriched with live data from other services
public DeliveryMonitorResponse getByDeliveryId(String deliveryId) {

  DeliveryMonitor monitor = repository.findByDeliveryId(deliveryId)
          .orElseThrow(() -> new AdminServiceException(
                  "Delivery not found: " + deliveryId));

  // ── Feign call: get live delivery details from Delivery Service ──
  DeliveryDto liveDelivery = deliveryClient.getDeliveryById(
          deliveryId);

  // ── Feign call: get latest tracking status from Tracking Service ──
  TrackingDto latestTracking = trackingClient.getLatestStatus(
          monitor.getTrackingNumber());

  // build response with both local + live data in one builder chain
  return DeliveryMonitorResponse.builder()
          // ── local DB fields ──
          .id(monitor.getId())
          .deliveryId(monitor.getDeliveryId())
          .trackingNumber(monitor.getTrackingNumber())
          .customerName(monitor.getCustomerName())
          .senderCity(monitor.getSenderCity())
          .receiverCity(monitor.getRecieverCity())
          .currentStatus(monitor.getCurrentStatus())
          .assignedHub(monitor.getAssignedHub())
          .lastUpdated(monitor.getLastUpdated())
          // ── live fields from Feign ──
          .liveSenderName(liveDelivery.getSenderName())
          .liveReceiverName(liveDelivery.getReceiverName())
          .latestTrackingStatus(latestTracking.getStatus())
          .latestTrackingLocation(latestTracking.getLocation())
          .build();
}

 // Called by Delivery Service when status changes — keeps admin snapshot in sync
 public DeliveryMonitorResponse updateStatus(
         String deliveryId, DeliveryStatus newStatus) {

     DeliveryMonitor monitor = repository.findByDeliveryId(deliveryId)
             .orElseThrow(() -> new AdminServiceException(
                     "Delivery not found: " + deliveryId));

     monitor.setCurrentStatus(newStatus);
     return toResponse(repository.save(monitor));
 }
 public DeliveryMonitorResponse syncDelivery(DeliveryMonitorRequest request) {
	    DeliveryMonitor monitor = repository
	            .findByDeliveryId(request.getDeliveryId())
	            .orElse(new DeliveryMonitor());

	    monitor.setDeliveryId(request.getDeliveryId());
	    monitor.setTrackingNumber(request.getTrackingNumber());
	    monitor.setCustomerName(request.getCustomerName());
	    monitor.setSenderCity(request.getSenderCity());
	    monitor.setRecieverCity(request.getReceiverCity());
	    monitor.setCurrentStatus(request.getCurrentStatus());
	    monitor.setAssignedHub(request.getAssignedHub());

	    return toResponse(repository.save(monitor));
	}
 private DeliveryMonitorResponse toResponse(DeliveryMonitor d) {
     return DeliveryMonitorResponse.builder()
             .id(d.getId())
             .deliveryId(d.getDeliveryId())
             .trackingNumber(d.getTrackingNumber())
             .customerName(d.getCustomerName())
             .senderCity(d.getSenderCity())
             .receiverCity(d.getRecieverCity())
             .currentStatus(d.getCurrentStatus())
             .assignedHub(d.getAssignedHub())
             .lastUpdated(d.getLastUpdated())
             .build();
 }
}