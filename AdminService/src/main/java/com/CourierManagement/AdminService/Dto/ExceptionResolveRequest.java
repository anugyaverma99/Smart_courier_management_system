package com.CourierManagement.AdminService.Dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class ExceptionResolveRequest {
 
	@NotBlank(message = "Remarks are required")
	private String remarks;
	@NotBlank(message = "Resolved by is required")
 private String resolvedBy;
}
