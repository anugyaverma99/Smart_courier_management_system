package com.CourierManagement.DeliveryService.Entity;


import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String name;
    private String phone;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
