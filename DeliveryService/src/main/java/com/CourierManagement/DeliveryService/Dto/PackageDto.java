package com.CourierManagement.DeliveryService.Dto;


import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class PackageDto {
 private String description;
 private double weightKg;
 private double lengthCm;
 private double widthCm;
 private double heightCm;
 private String serviceType;   // domestic / express / international
 private double declaredValue;
}
