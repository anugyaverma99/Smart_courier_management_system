package com.CourierManagement.AdminService.Dto;


import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class HubResponse {
 private Long id;
 private String name;
 private String city;
 private String state;
 private String pincode;
 private String contactNumber;
 private boolean active;
 private LocalDateTime createdAt;
}