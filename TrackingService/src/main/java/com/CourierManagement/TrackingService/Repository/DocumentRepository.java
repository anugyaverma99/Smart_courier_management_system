package com.CourierManagement.TrackingService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.TrackingService.Entity.Document;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

 List<Document> findByDeliveryIdOrderByUploadedAtDesc(String deliveryId);


 List<Document> findByTrackingNumberOrderByUploadedAtDesc(String trackingNumber);
}
