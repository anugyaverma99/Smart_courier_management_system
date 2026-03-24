package com.CourierManagement.TrackingService.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.TrackingService.Entity.DeliveryProof;

import java.util.Optional;

@Repository
public interface DeliveryProofRepository extends JpaRepository<DeliveryProof, Long> {

 
 Optional<DeliveryProof> findByDeliveryId(String deliveryId);

 
 Optional<DeliveryProof> findByTrackingNumber(String trackingNumber);
}