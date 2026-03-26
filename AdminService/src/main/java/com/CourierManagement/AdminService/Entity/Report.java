package com.CourierManagement.AdminService.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Report {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 // "DAILY", "WEEKLY", "MONTHLY"
 @Column(name = "report_type", nullable = false)
 private String reportType;

 @Column(name = "from_date", nullable = false)
 private LocalDate fromDate;

 @Column(name = "to_date", nullable = false)
 private LocalDate toDate;

 private int totalDeliveries;
 private int deliveredCount;
 private int failedCount;
 private int delayedCount;
 private int returnedCount;
 private int liveDeliveryCount;
 private int totalTrackingEvents;

 @Column(name = "generated_by")
 private String generatedBy;

 @Column(name = "generated_at", updatable = false)
 private LocalDateTime generatedAt;

 @PrePersist
 public void prePersist() {
     this.generatedAt = LocalDateTime.now();
 }
}