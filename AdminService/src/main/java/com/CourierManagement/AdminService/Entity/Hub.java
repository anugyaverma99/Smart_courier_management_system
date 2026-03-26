package com.CourierManagement.AdminService.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hubs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Hub {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, unique = true)
 private String name;

 private String city;
 private String state;
 private String pincode;
 private String contactNumber;
 private boolean active;

 @Column(name = "created_at", updatable = false)
 private LocalDateTime createdAt;

 @PrePersist
 public void prePersist() {
     this.createdAt = LocalDateTime.now();
     this.active = true;
 }
}
