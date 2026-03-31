package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.ReportResponse;
import com.CourierManagement.AdminService.Service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService service;

    @InjectMocks
    private ReportController reportController;

    @Test
    void generateReport_success() {
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        ReportResponse mockResponse = new ReportResponse();

        when(service.generateReport("MONTHLY", from, to, "admin1"))
                .thenReturn(mockResponse);

        ResponseEntity<ReportResponse> result =
                reportController.generateReport("MONTHLY", from, to, "admin1");

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(service, times(1))
                .generateReport("MONTHLY", from, to, "admin1");
    }

    @Test
    void generateReport_dailyReport() {
        LocalDate from = LocalDate.of(2026, 3, 30);
        LocalDate to = LocalDate.of(2026, 3, 30);
        ReportResponse mockResponse = new ReportResponse();

        when(service.generateReport("DAILY", from, to, "admin2"))
                .thenReturn(mockResponse);

        ResponseEntity<ReportResponse> result =
                reportController.generateReport("DAILY", from, to, "admin2");

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void getReports_success() {
        when(service.getReports("MONTHLY"))
                .thenReturn(List.of(new ReportResponse()));

        ResponseEntity<List<ReportResponse>> result =
                reportController.getReports("MONTHLY");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(service, times(1)).getReports("MONTHLY");
    }

    @Test
    void getReports_emptyList() {
        when(service.getReports("WEEKLY")).thenReturn(List.of());

        ResponseEntity<List<ReportResponse>> result =
                reportController.getReports("WEEKLY");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, result.getBody().size());
    }
}