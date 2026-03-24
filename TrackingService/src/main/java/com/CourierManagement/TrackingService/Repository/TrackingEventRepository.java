package com.CourierManagement.TrackingService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.TrackingService.Entity.TrackingEvent;

import java.util.List;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {


 List<TrackingEvent> findByTrackingNumberOrderByEventTimeAsc(String trackingNumber);

 TrackingEvent findTopByTrackingNumberOrderByEventTimeDesc(String trackingNumber);

 List<TrackingEvent> findByDeliveryIdOrderByEventTimeAsc(String deliveryId);
 
}

