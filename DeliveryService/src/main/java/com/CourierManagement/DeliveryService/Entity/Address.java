package com.CourierManagement.DeliveryService.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="addresses")

public class Address {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String name;
    private String phone;
    private String addressLine; 
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
