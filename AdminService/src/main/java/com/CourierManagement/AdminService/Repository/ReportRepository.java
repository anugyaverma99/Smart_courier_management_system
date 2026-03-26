package com.CourierManagement.AdminService.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.CourierManagement.AdminService.Entity.Report;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

 List<Report> findByReportTypeOrderByGeneratedAtDesc(String reportType);
}