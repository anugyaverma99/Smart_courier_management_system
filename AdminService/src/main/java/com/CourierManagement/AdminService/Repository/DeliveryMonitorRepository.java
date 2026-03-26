package com.CourierManagement.AdminService.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.AdminService.Entity.DeliveryMonitor;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryMonitorRepository
     extends JpaRepository<DeliveryMonitor, Long> {

 Optional<DeliveryMonitor> findByDeliveryId(String deliveryId);

 // All deliveries at a specific hub
 List<DeliveryMonitor> findByAssignedHub(String hubName);

 // All deliveries with a given status
 List<DeliveryMonitor> findByCurrentStatus(DeliveryStatus status);

 // Count by status 
 long countByCurrentStatus(DeliveryStatus status);
}
