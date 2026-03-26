package com.CourierManagement.AdminService.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.AdminService.Entity.DeliveryException;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;

import java.util.List;

@Repository
public interface DeliveryExceptionRepository
     extends JpaRepository<DeliveryException, Long> {

 // All open exceptions — admin sees these first
 List<DeliveryException> findByResolutionStatus(ExceptionStatus status);

 // Exceptions for a specific delivery
 List<DeliveryException> findByDeliveryId(String deliveryId);

 // Count open exceptions — used by dashboard
 long countByResolutionStatus(ExceptionStatus status);
}
