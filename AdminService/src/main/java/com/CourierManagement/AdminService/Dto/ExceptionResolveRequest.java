package com.CourierManagement.AdminService.Dto;


import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class ExceptionResolveRequest {
 private String remarks;
 private String resolvedBy;
}
