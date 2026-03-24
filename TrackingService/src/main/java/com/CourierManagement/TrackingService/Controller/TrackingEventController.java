package com.CourierManagement.TrackingService.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CourierManagement.TrackingService.Dto.TrackingEventRequest;
import com.CourierManagement.TrackingService.Dto.TrackingEventResponse;
import com.CourierManagement.TrackingService.Service.TrackingEventService;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingEventController {

 private final TrackingEventService service;

 @PostMapping("/events")
 public ResponseEntity<TrackingEventResponse> addEvent(
         @RequestBody TrackingEventRequest request) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.addEvent(request));
 }


 @GetMapping("/{trackingNumber}")
 public ResponseEntity<List<TrackingEventResponse>> getTimeline(
         @PathVariable String trackingNumber) {
     return ResponseEntity.ok(service.getTimeline(trackingNumber));
 }


 @GetMapping("/{trackingNumber}/latest")
 public ResponseEntity<TrackingEventResponse> getLatest(
         @PathVariable String trackingNumber) {
     return ResponseEntity.ok(service.getLatestStatus(trackingNumber));
 }
}