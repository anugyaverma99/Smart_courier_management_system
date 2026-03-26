package com.CourierManagement.AdminService.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.CourierManagement.AdminService.Dto.DeliveryDto;


@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/deliveries/{id}")
    DeliveryDto getDeliveryById(@PathVariable("id") String id);

    @PutMapping("/deliveries/{id}/status")
    void updateDeliveryStatus(@PathVariable("id") Long id,
                              @RequestParam String status);

    @GetMapping("/deliveries")
    List<DeliveryDto> getAllDeliveries();
    
    @GetMapping("/deliveries/{id}/exists")
    boolean doesDeliveryExist(@PathVariable("id") String id);

	
}
