package com.CourierManagement.DeliveryService.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "admin-service")
public interface AdminClient {

    // notify Admin when delivery status changes
    @PutMapping("/admin/monitor/{deliveryId}/status")
    void updateMonitorStatus(@PathVariable("deliveryId") String deliveryId,
                             @RequestParam String status);
}