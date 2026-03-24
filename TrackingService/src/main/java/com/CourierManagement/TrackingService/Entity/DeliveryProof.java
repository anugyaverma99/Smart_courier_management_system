package com.CourierManagement.TrackingService.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_proof")
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class DeliveryProof {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 
 @Column(name = "delivery_id", nullable = false, unique = true)
 private String deliveryId;

 @Column(name = "tracking_number", nullable = false)
 private String trackingNumber;

 
 @Column(name = "received_by", nullable = false)
 private String receivedBy;

 
 @Column(name = "proof_image_path")
 private String proofImagePath;

 
 private String remarks;

 
 @Column(name = "submitted_by")
 private String submittedBy;

 @Column(name = "delivered_at", nullable = false)
 private LocalDateTime deliveredAt;

 @Column(name = "created_at", nullable = false, updatable = false)
 private LocalDateTime createdAt;

 @PrePersist
 public void prePersist() {
     this.createdAt = LocalDateTime.now();
     if (this.deliveredAt == null) {
         this.deliveredAt = LocalDateTime.now();
     }
 }
}