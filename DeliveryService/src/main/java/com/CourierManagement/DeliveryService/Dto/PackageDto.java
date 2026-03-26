package com.CourierManagement.DeliveryService.Dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class PackageDto { 
	
	@NotBlank(message = "Description is required")
private String description;

@Min(value = 0, message = "Weight must be greater than 0")
private double weightKg;

@Min(value = 0, message = "Length must be greater than 0")
private double lengthCm;

@Min(value = 0, message = "Width must be greater than 0")
private double widthCm;

@Min(value = 0, message = "Height must be greater than 0")
private double heightCm;

@NotBlank(message = "Service type is required")
@Pattern(regexp = "^(domestic|express|international)$",
         message = "Service type must be domestic, express or international")
private String serviceType;

@Min(value = 0, message = "Declared value must be positive")
private double declaredValue;
}
