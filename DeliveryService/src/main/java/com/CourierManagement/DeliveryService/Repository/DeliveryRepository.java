package com.CourierManagement.DeliveryService.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CourierManagement.DeliveryService.Entity.Delivery;
import com.CourierManagement.DeliveryService.Entity.DeliveryStatus;


public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
	List<Delivery> findByCustomerIdOrderByCreatedAtDesc(String customerId);

    
    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    
    List<Delivery> findByStatus(DeliveryStatus status);

}