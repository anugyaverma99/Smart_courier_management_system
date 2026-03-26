package com.CourierManagement.TrackingService.Controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.CourierManagement.TrackingService.Dto.DeliveryProofRequest;
import com.CourierManagement.TrackingService.Dto.DeliveryProofResponse;
import com.CourierManagement.TrackingService.Service.DeliveryProofService;

import java.io.IOException;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class DeliveryProofController {

 private final DeliveryProofService service;


 @PostMapping(value = "/proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<DeliveryProofResponse> submitProof(
         @RequestParam("deliveryId")     String deliveryId,
         @RequestParam("trackingNumber") String trackingNumber,
         @RequestParam("receivedBy")     String receivedBy,
         @RequestParam("submittedBy")    String submittedBy,
         @RequestParam(value = "remarks", required = false) String remarks,
         @RequestParam(value = "proofImage", required = false) MultipartFile proofImage)
         throws IOException {

     DeliveryProofRequest request = DeliveryProofRequest.builder()
             .deliveryId(deliveryId)
             .trackingNumber(trackingNumber)
             .receivedBy(receivedBy)
             .submittedBy(submittedBy)
             .remarks(remarks)
             .build();

     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.submitProof(request, proofImage));
 }

  @GetMapping("/{deliveryId}/proof")
 public ResponseEntity<DeliveryProofResponse> getProof(
         @PathVariable String deliveryId) {
     return ResponseEntity.ok(service.getProof(deliveryId));
 }
}