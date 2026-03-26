package com.CourierManagement.DeliveryService.Dto;


import java.time.LocalDateTime;

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
 private String customerId;
 private AddressDto senderAddress;      // step 1
 private AddressDto receiverAddress;    // step 2
 private PackageDto packageDetails;     // step 3
 private LocalDateTime pickupScheduledAt;
}
