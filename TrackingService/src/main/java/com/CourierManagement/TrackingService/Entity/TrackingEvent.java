package com.CourierManagement.TrackingService.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_events")
@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class TrackingEvent {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @Column(name = "delivery_id", nullable = false)
 private String deliveryId;

 @Column(name = "tracking_number", nullable = false)
 private String trackingNumber;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private TrackingStatus status;

 @Column(nullable = false)
 private String location;

 private String remarks;


 private String updatedBy;

 @Column(name = "event_time", nullable = false) //nullable = false means:Database WILL NOT allow NULL values
 private LocalDateTime eventTime;

 @Column(name = "created_at", nullable = false, updatable = false)
 private LocalDateTime createdAt;

 @PrePersist
 public void prePersist() { // method name can be anything ,just annotation matters
     this.createdAt = LocalDateTime.now();
     if (this.eventTime == null) {
         this.eventTime = LocalDateTime.now();
     }
     
     // @PrePersist - annotation in jpa that tells hibernate to run this code before inserting entity into database
 }
}