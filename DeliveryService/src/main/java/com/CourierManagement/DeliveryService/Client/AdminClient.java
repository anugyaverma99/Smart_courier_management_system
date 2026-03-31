package com.CourierManagement.DeliveryService.Client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.CourierManagement.DeliveryService.Dto.DeliveryMonitorRequest;

@FeignClient(name = "admin-service")
public interface AdminClient {

    // notify Admin when delivery status changes
    @PutMapping("/admin/monitor/{deliveryId}/status")
    void updateMonitorStatus(@PathVariable("deliveryId") String deliveryId,
                             @RequestParam String status);
    @PostMapping("/admin/deliveries/sync")
    void syncDelivery(@RequestBody DeliveryMonitorRequest request);
}