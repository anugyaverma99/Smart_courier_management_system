package com.CourierManagement.TrackingService.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.CourierManagement.TrackingService.Dto.DocumentResponse;
import com.CourierManagement.TrackingService.Service.DocumentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tracking/documents")
@RequiredArgsConstructor
public class DocumentController {

 private final DocumentService service;


 @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
 public ResponseEntity<DocumentResponse> upload(
         @RequestParam("deliveryId")     String deliveryId,
         @RequestParam("trackingNumber") String trackingNumber,
         @RequestParam("documentType")   String documentType,
         @RequestParam("uploadedBy")     String uploadedBy,
         @RequestParam("file")           MultipartFile file) throws IOException {

     return ResponseEntity
             .status(HttpStatus.CREATED)
             .body(service.uploadDocument(
                     deliveryId, trackingNumber, documentType, uploadedBy, file));
 }

 
 @GetMapping("/{deliveryId}")
 public ResponseEntity<List<DocumentResponse>> getByDelivery(
         @PathVariable String deliveryId) {
     return ResponseEntity.ok(service.getDocumentsByDelivery(deliveryId));
 }
}