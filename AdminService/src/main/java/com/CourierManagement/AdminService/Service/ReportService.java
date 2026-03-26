package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.CourierManagement.AdminService.Dto.ReportResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.Report;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import com.CourierManagement.AdminService.Repository.ReportRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

 private final ReportRepository reportRepository;
 private final DeliveryMonitorRepository deliveryMonitorRepository;


 public ReportResponse generateReport(
         String reportType, LocalDate fromDate,
         LocalDate toDate, String generatedBy) {

     
     Report report = Report.builder()
             .reportType(reportType)
             .fromDate(fromDate)
             .toDate(toDate)
             .totalDeliveries((int) deliveryMonitorRepository.count())
             .deliveredCount((int) deliveryMonitorRepository
                     .countByCurrentStatus(DeliveryStatus.DELIVERED))
             .failedCount((int) deliveryMonitorRepository
                     .countByCurrentStatus(DeliveryStatus.FAILED))
             .delayedCount((int) deliveryMonitorRepository
                     .countByCurrentStatus(DeliveryStatus.DELAYED))
             .returnedCount((int) deliveryMonitorRepository
                     .countByCurrentStatus(DeliveryStatus.RETURNED))
             .generatedBy(generatedBy)
             .build();

     return toResponse(reportRepository.save(report));
 }

 
 public List<ReportResponse> getReports(String reportType) {
     return reportRepository
             .findByReportTypeOrderByGeneratedAtDesc(reportType)
             .stream()
             .map(this::toResponse)
             .collect(Collectors.toList());
 }

 private ReportResponse toResponse(Report r) {
     return ReportResponse.builder()
             .id(r.getId())
             .reportType(r.getReportType())
             .fromDate(r.getFromDate())
             .toDate(r.getToDate())
             .totalDeliveries(r.getTotalDeliveries())
             .deliveredCount(r.getDeliveredCount())
             .failedCount(r.getFailedCount())
             .delayedCount(r.getDelayedCount())
             .returnedCount(r.getReturnedCount())
             .generatedBy(r.getGeneratedBy())
             .generatedAt(r.getGeneratedAt())
             .build();
 }
}