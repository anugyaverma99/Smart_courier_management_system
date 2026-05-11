package com.CourierManagement.AdminService.Service;

import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Client.TrackingClient;
import com.CourierManagement.AdminService.Dto.DeliveryDto;
import com.CourierManagement.AdminService.Dto.ReportResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.Report;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import com.CourierManagement.AdminService.Repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private DeliveryMonitorRepository deliveryMonitorRepository;

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private TrackingClient trackingClient;

    @InjectMocks
    private ReportService reportService;

    @Test
    void generateReport_success() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        Report savedReport = new Report();
        savedReport.setId(1L);
        savedReport.setReportType("MONTHLY");
        savedReport.setFromDate(from);
        savedReport.setToDate(to);
        savedReport.setTotalDeliveries(100);
        savedReport.setDeliveredCount(60);
        savedReport.setFailedCount(10);
        savedReport.setDelayedCount(20);
        savedReport.setReturnedCount(10);
        savedReport.setLiveDeliveryCount(2);
        savedReport.setTotalTrackingEvents(200);
        savedReport.setGeneratedBy("admin1");
        savedReport.setGeneratedAt(LocalDateTime.now());

        DeliveryDto mockDto = mock(DeliveryDto.class);
        when(deliveryClient.getAllDeliveries()).thenReturn(List.of(mockDto)); when(trackingClient.getTotalEventCount()).thenReturn(200L);
        when(deliveryMonitorRepository.count()).thenReturn(100L);
        when(reportRepository.save(any(Report.class))).thenReturn(savedReport);

        ReportResponse response = reportService.generateReport("MONTHLY", from, to, "admin1");

        assertNotNull(response);
        assertEquals("MONTHLY", response.getReportType());
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(deliveryClient, times(1)).getAllDeliveries();
        verify(trackingClient, times(1)).getTotalEventCount();
    }

    @Test
    void generateReport_withNoDeliveries() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        Report emptyReport = new Report();
        emptyReport.setId(2L);
        emptyReport.setReportType("DAILY");
        emptyReport.setFromDate(from);
        emptyReport.setToDate(to);
        emptyReport.setTotalDeliveries(0);
        emptyReport.setGeneratedAt(LocalDateTime.now());

        when(deliveryClient.getAllDeliveries()).thenReturn(List.of());
        when(trackingClient.getTotalEventCount()).thenReturn(0L);
        when(deliveryMonitorRepository.count()).thenReturn(0L);
        when(reportRepository.save(any(Report.class))).thenReturn(emptyReport);

        ReportResponse response = reportService.generateReport("DAILY", from, to, "admin1");

        assertNotNull(response);
        assertEquals(0, response.getTotalDeliveries());
    }

    @Test
    void getReports_success() {
        Report report = new Report();
        report.setId(1L);
        report.setReportType("MONTHLY");
        report.setGeneratedAt(LocalDateTime.now());

        when(reportRepository.findByReportTypeOrderByGeneratedAtDesc("MONTHLY"))
                .thenReturn(List.of(report));

        List<ReportResponse> responses = reportService.getReports("MONTHLY");

        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        verify(reportRepository, times(1))
                .findByReportTypeOrderByGeneratedAtDesc("MONTHLY");
    }

    @Test
    void getReports_emptyResult() {
        when(reportRepository.findByReportTypeOrderByGeneratedAtDesc("WEEKLY"))
                .thenReturn(List.of());

        List<ReportResponse> responses = reportService.getReports("WEEKLY");

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }
}