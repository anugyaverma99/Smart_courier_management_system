package com.CourierManagement.AuthService.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class User {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, unique = true)
 private String email;

 @Column(nullable = false)
 private String password;  

 @Column(nullable = false)
 private String fullName;

 private String phone;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private Role role;        

 private boolean active;

 @Column(name = "created_at", updatable = false)
 private LocalDateTime createdAt;

 @PrePersist
 public void prePersist() {
     this.createdAt = LocalDateTime.now();
     this.active = true;
     if (this.role == null) {
         this.role = Role.CUSTOMER; 
     }
 }
}
