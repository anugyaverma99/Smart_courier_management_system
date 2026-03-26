package com.CourierManagement.DeliveryService.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "packages")
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDetails {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String description;   
 private double weightKg;
 private double lengthCm;
 private double widthCm;
 private double heightCm;

 // domestic / express / international 
 private String serviceType;

 private double declaredValue; // for insurance / customs
}