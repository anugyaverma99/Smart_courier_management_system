package com.CourierManagement.DeliveryService.Dto;

import com.CourierManagement.DeliveryService.Entity.DeliveryStatus;

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
public class UpdateStatusRequest {
 private DeliveryStatus status;
 private String updatedBy;
 private String remarks;
 private String location;
}
