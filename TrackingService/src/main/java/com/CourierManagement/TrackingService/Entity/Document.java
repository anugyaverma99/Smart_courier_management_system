package com.CourierManagement.TrackingService.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Document {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "delivery_id", nullable = false)
 private String deliveryId;

 @Column(name = "tracking_number", nullable = false)
 private String trackingNumber;


 @Column(name = "file_name", nullable = false)
 private String fileName;


 @Column(name = "file_path", nullable = false)
 private String filePath;


 @Column(name = "document_type")
 private String documentType;


 private String contentType;

 @Column(name = "uploaded_by")
 private String uploadedBy;

 @Column(name = "uploaded_at", nullable = false, updatable = false)
 private LocalDateTime uploadedAt;

 @PrePersist
 public void prePersist() {
     this.uploadedAt = LocalDateTime.now();
 }
}

