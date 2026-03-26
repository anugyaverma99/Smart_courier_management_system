package com.CourierManagement.TrackingService.Dto;


import com.CourierManagement.TrackingService.Entity.TrackingStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TrackingEventRequest {

	 @NotBlank(message = "Delivery ID is required")
	    private String deliveryId;

	    @NotBlank(message = "Tracking number is required")
	    private String trackingNumber;

	    @NotNull(message = "Status is required")
	    private TrackingStatus status;

	    @NotBlank(message = "Location is required")
	    private String location;
 private String remarks;
 private String updatedBy;

}