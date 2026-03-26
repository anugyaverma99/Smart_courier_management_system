package com.CourierManagement.AdminService.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_exceptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryException {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "delivery_id", nullable = false)
 private String deliveryId;

 @Column(name = "tracking_number")
 private String trackingNumber;

 // What status caused this exception (DELAYED / FAILED / RETURNED)
 @Enumerated(EnumType.STRING)
 @Column(name = "exception_status", nullable = false)
 private DeliveryStatus exceptionStatus;

 // Is this exception open, in progress, or resolved
 @Enumerated(EnumType.STRING)
 @Column(name = "resolution_status", nullable = false)
 private ExceptionStatus resolutionStatus;

 private String reason;
 private String remarks;

 // Admin who resolved it
 @Column(name = "resolved_by")
 private String resolvedBy;

 @Column(name = "raised_at", nullable = false, updatable = false)
 private LocalDateTime raisedAt;

 @Column(name = "resolved_at")
 private LocalDateTime resolvedAt;

 @PrePersist
 public void prePersist() {
     this.raisedAt = LocalDateTime.now();
     if (this.resolutionStatus == null) {
         this.resolutionStatus = ExceptionStatus.OPEN;
     }
 }
}
