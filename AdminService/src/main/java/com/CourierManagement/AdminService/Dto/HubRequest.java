package com.CourierManagement.AdminService.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubRequest {
 private String name;
 private String city;
 private String state;
 private String pincode;
 private String contactNumber;
}