package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Client.TrackingClient;
import com.CourierManagement.AdminService.Dto.DeliveryDto;
import com.CourierManagement.AdminService.Dto.ReportResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.Report;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import com.CourierManagement.AdminService.Repository.ReportRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final DeliveryMonitorRepository deliveryMonitorRepository;
    private final DeliveryClient deliveryClient;    // ← calls Delivery Service
    private final TrackingClient trackingClient;    // ← calls Tracking Service

    public ReportResponse generateReport(
            String reportType, LocalDate fromDate,
            LocalDate toDate, String generatedBy) {

        // Convert dates to LocalDateTime for range query
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(23, 59, 59);

        List<DeliveryDto> liveDeliveries = new ArrayList<>();
        long totalTrackingEvents = 0;

        try {
            liveDeliveries = deliveryClient.getAllDeliveries();
        } catch (Exception e) {
            log.warn("Could not fetch live deliveries: {}", e.getMessage());
        }

        try {
            totalTrackingEvents = trackingClient.getTotalEventCount();
        } catch (Exception e) {
            log.warn("Could not fetch tracking count: {}", e.getMessage());
        }

        Report report = Report.builder()
                .reportType(reportType)
                .fromDate(fromDate)
                .toDate(toDate)
                // now filtered by date range
                .totalDeliveries((int) deliveryMonitorRepository
                        .countByCreatedAtBetween(from, to))
                .deliveredCount((int) deliveryMonitorRepository
                        .countByCurrentStatusAndCreatedAtBetween(DeliveryStatus.DELIVERED, from, to))
                .failedCount((int) deliveryMonitorRepository
                        .countByCurrentStatusAndCreatedAtBetween(DeliveryStatus.FAILED, from, to))
                .delayedCount((int) deliveryMonitorRepository
                        .countByCurrentStatusAndCreatedAtBetween(DeliveryStatus.DELAYED, from, to))
                .returnedCount((int) deliveryMonitorRepository
                        .countByCurrentStatusAndCreatedAtBetween(DeliveryStatus.RETURNED, from, to))
                .liveDeliveryCount(liveDeliveries.size())
                .totalTrackingEvents((int) totalTrackingEvents)
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
                .liveDeliveryCount(r.getLiveDeliveryCount())
                .totalTrackingEvents(r.getTotalTrackingEvents())
                .generatedBy(r.getGeneratedBy())
                .generatedAt(r.getGeneratedAt())
                .build();
    }
}