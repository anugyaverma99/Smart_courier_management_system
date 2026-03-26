package com.CourierManagement.TrackingService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.CourierManagement.TrackingService.Client.DeliveryClient;
import com.CourierManagement.TrackingService.Dto.TrackingEventRequest;
import com.CourierManagement.TrackingService.Dto.TrackingEventResponse;
import com.CourierManagement.TrackingService.Entity.TrackingEvent;
import com.CourierManagement.TrackingService.Exception.TrackingNotFoundException;
import com.CourierManagement.TrackingService.Repository.TrackingEventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackingEventService {

 private final TrackingEventRepository repository;
 private final DeliveryClient deliveryClient;

 public TrackingEventResponse addEvent(TrackingEventRequest request) {
	 boolean exists = deliveryClient.doesDeliveryExist(request.getDeliveryId());
     if (!exists) {
         throw new TrackingNotFoundException(
             "Delivery not found with ID: " + request.getDeliveryId());
     }
	 // DTO->ENTITY
     TrackingEvent event = TrackingEvent.builder()
             .deliveryId(request.getDeliveryId())
             .trackingNumber(request.getTrackingNumber())
             .status(request.getStatus())
             .location(request.getLocation())
             .remarks(request.getRemarks())
             .updatedBy(request.getUpdatedBy())
             .eventTime(LocalDateTime.now())
             .build();

     return toResponse(repository.save(event));
 }

 
 public List<TrackingEventResponse> getTimeline(String trackingNumber) {
     List<TrackingEvent> events =
             repository.findByTrackingNumberOrderByEventTimeAsc(trackingNumber);

     if (events.isEmpty()) {
         throw new TrackingNotFoundException(
             "No tracking events found for: " + trackingNumber);
     }
     return events.stream().map(this::toResponse).collect(Collectors.toList());
 }


 public TrackingEventResponse getLatestStatus(String trackingNumber) {
     TrackingEvent event =
             repository.findTopByTrackingNumberOrderByEventTimeDesc(trackingNumber);

     if (event == null) {
         throw new TrackingNotFoundException(
             "No tracking events found for: " + trackingNumber);
     }
     return toResponse(event);
 }

 
 private TrackingEventResponse toResponse(TrackingEvent e) {
	 //ENTITY->DTO
     return TrackingEventResponse.builder()
             .id(e.getId())
             .deliveryId(e.getDeliveryId())
             .trackingNumber(e.getTrackingNumber())
             .status(e.getStatus())
             .location(e.getLocation())
             .remarks(e.getRemarks())
             .updatedBy(e.getUpdatedBy())
             .eventTime(e.getEventTime())
             .createdAt(e.getCreatedAt())
             .build();
 }
}