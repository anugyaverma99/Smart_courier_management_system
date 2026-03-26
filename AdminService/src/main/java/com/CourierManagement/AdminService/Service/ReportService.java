package com.CourierManagement.AdminService.Service;

import lombok.RequiredArgsConstructor;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final DeliveryMonitorRepository deliveryMonitorRepository;
    private final DeliveryClient deliveryClient;    // ← calls Delivery Service
    private final TrackingClient trackingClient;    // ← calls Tracking Service

    public ReportResponse generateReport(
            String reportType, LocalDate fromDate,
            LocalDate toDate, String generatedBy) {

        // ── Feign call: get live delivery list from Delivery Service ──
        // used to cross-check counts with live data
        List<DeliveryDto> liveDeliveries = deliveryClient.getAllDeliveries();

        // ── Feign call: get total tracking events from Tracking Service ──
        long totalTrackingEvents = trackingClient.getTotalEventCount();

        Report report = Report.builder()
                .reportType(reportType)
                .fromDate(fromDate)
                .toDate(toDate)
                // local DB counts — admin's own snapshot
                .totalDeliveries((int) deliveryMonitorRepository.count())
                .deliveredCount((int) deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.DELIVERED))
                .failedCount((int) deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.FAILED))
                .delayedCount((int) deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.DELAYED))
                .returnedCount((int) deliveryMonitorRepository
                        .countByCurrentStatus(DeliveryStatus.RETURNED))
                // live counts from other services via Feign
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