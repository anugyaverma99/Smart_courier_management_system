package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.CourierManagement.AdminService.Dto.DeliveryMonitorResponse;
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
 public DeliveryMonitorResponse getByDeliveryId(String deliveryId) {
     return repository.findByDeliveryId(deliveryId)
             .map(this::toResponse)
             .orElseThrow(() -> new AdminServiceException(
                     "Delivery not found: " + deliveryId));
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