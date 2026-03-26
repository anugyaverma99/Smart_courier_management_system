package com.CourierManagement.AdminService.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.AdminService.Entity.Hub;

import java.util.List;

@Repository
public interface HubRepository extends JpaRepository<Hub, Long> {

 List<Hub> findByActiveTrue();
 long countByActiveTrue();
}