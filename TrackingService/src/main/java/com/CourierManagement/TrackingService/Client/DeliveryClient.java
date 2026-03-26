package com.CourierManagement.TrackingService.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/deliveries/{id}/exists")
    boolean doesDeliveryExist(@PathVariable("id") String id);
}
