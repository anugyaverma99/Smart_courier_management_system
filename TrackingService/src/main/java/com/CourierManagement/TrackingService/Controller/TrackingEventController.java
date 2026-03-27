package com.CourierManagement.TrackingService.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.CourierManagement.TrackingService.Dto.TrackingEventRequest;
import com.CourierManagement.TrackingService.Dto.TrackingEventResponse;
import com.CourierManagement.TrackingService.Service.TrackingEventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
@Tag(name = "Tracking", description = "Tracking event APIs")
public class TrackingEventController {

 private final TrackingEventService service;

 @PostMapping("/events")
 @Operation(summary = "Add tracking event", description = "Record a new status event in delivery timeline")
 
 public ResponseEntity<TrackingEventResponse> addEvent(
        @Valid @RequestBody TrackingEventRequest request) {
     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.addEvent(request));
 }


 @GetMapping("/{trackingNumber}")
 @Operation(summary = "Get timeline", description = "Get full tracking history for a delivery")
 
 public ResponseEntity<List<TrackingEventResponse>> getTimeline(
         @PathVariable String trackingNumber) {
     return ResponseEntity.ok(service.getTimeline(trackingNumber));
 }


 @GetMapping("/{trackingNumber}/latest")
 @Operation(summary = "Get latest status", description = "Get most recent tracking event")
 
 public ResponseEntity<TrackingEventResponse> getLatest(
         @PathVariable String trackingNumber) {
     return ResponseEntity.ok(service.getLatestStatus(trackingNumber));
 }
}