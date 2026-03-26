package com.CourierManagement.DeliveryService.Dto;


import java.time.LocalDateTime;

import jakarta.validation.Valid;
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
public class CreateDeliveryRequest {
	 @NotBlank(message = "Customer ID is required")
	    private String customerId;

	    @Valid
	    @NotNull(message = "Sender address is required")
	    private AddressDto senderAddress;

	    @Valid
	    @NotNull(message = "Receiver address is required")
	    private AddressDto receiverAddress;

	    @Valid
	    @NotNull(message = "Package details are required")
	    private PackageDto packageDetails;   // step 3
 private LocalDateTime pickupScheduledAt;
}
