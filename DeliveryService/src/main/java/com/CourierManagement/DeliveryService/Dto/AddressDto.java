package com.CourierManagement.DeliveryService.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class AddressDto {
    private String name;
    private String phone;
    private String email;
    private String addressLine;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
