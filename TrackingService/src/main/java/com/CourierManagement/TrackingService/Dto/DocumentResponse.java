package com.CourierManagement.TrackingService.Dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponse {
 private Long id;
 private String deliveryId;
 private String trackingNumber;
 private String fileName;
 private String filePath;
 private String documentType;
 private String contentType;
 private String uploadedBy;
 private LocalDateTime uploadedAt;
}