package com.CourierManagement.DeliveryService.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.CourierManagement.DeliveryService.Entity.Delivery;


public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

	 List<Delivery> findByCustomerId(Long customerId);}
