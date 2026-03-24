package com.CourierManagement.TrackingService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.CourierManagement.TrackingService.Dto.DeliveryProofRequest;
import com.CourierManagement.TrackingService.Dto.DeliveryProofResponse;
import com.CourierManagement.TrackingService.Entity.DeliveryProof;
import com.CourierManagement.TrackingService.Exception.TrackingNotFoundException;
import com.CourierManagement.TrackingService.Repository.DeliveryProofRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryProofService {

 private final DeliveryProofRepository repository;

 @Value("${app.upload.dir:uploads/documents}")
 private String uploadDir;

 
 public DeliveryProofResponse submitProof(
         DeliveryProofRequest request,
         MultipartFile proofImage) throws IOException {

     String imagePath = null;

     
     if (proofImage != null && !proofImage.isEmpty()) {
         Path uploadPath = Paths.get(uploadDir, "proofs");
         Files.createDirectories(uploadPath);
         String uniqueName = UUID.randomUUID() + "_" + proofImage.getOriginalFilename();
         Path targetPath  = uploadPath.resolve(uniqueName);
         Files.copy(proofImage.getInputStream(), targetPath,
                    StandardCopyOption.REPLACE_EXISTING);
         imagePath = targetPath.toString();
     }

     DeliveryProof proof = DeliveryProof.builder()
             .deliveryId(request.getDeliveryId())
             .trackingNumber(request.getTrackingNumber())
             .receivedBy(request.getReceivedBy())
             .proofImagePath(imagePath)
             .remarks(request.getRemarks())
             .submittedBy(request.getSubmittedBy())
             .deliveredAt(request.getDeliveredAt())
             .build();

     return toResponse(repository.save(proof));
 }

 
 public DeliveryProofResponse getProof(String deliveryId) {
     return repository.findByDeliveryId(deliveryId)
             .map(this::toResponse)
             .orElseThrow(() -> new TrackingNotFoundException(
                     "No delivery proof found for: " + deliveryId));
 }

 private DeliveryProofResponse toResponse(DeliveryProof p) {
     return DeliveryProofResponse.builder()
             .id(p.getId())
             .deliveryId(p.getDeliveryId())
             .trackingNumber(p.getTrackingNumber())
             .receivedBy(p.getReceivedBy())
             .proofImagePath(p.getProofImagePath())
             .remarks(p.getRemarks())
             .submittedBy(p.getSubmittedBy())
             .deliveredAt( p.getDeliveredAt() != null 
            		    ? p.getDeliveredAt() 
            		    	    : LocalDateTime.now())
             .createdAt(p.getCreatedAt())
             .build();
 }
}
