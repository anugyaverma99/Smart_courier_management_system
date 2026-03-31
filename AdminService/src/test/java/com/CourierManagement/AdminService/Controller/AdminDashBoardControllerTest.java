package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.DashboardResponse;
import com.CourierManagement.AdminService.Service.AdminDashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardControllerTest {

    @Mock
    private AdminDashboardService service;

    @InjectMocks
    private AdminDashboardController adminDashboardController;

    @Test
    void getDashboard_success() {
        DashboardResponse mockResponse = DashboardResponse.builder()
                .totalDeliveries(100L)
                .deliveredToday(40L)
                .inTransit(30L)
                .outForDelivery(20L)
                .exceptions(5L)
                .activeHubs(10L)
                .build();

        when(service.getDashboard()).thenReturn(mockResponse);

        ResponseEntity<DashboardResponse> result =
                adminDashboardController.getDashboard();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(100L, result.getBody().getTotalDeliveries());
    }

    @Test
    void getDashboard_returnsOkStatus() {
        when(service.getDashboard())
                .thenReturn(DashboardResponse.builder().build());

        ResponseEntity<DashboardResponse> result =
                adminDashboardController.getDashboard();

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void getDashboard_callsServiceOnce() {
        when(service.getDashboard())
                .thenReturn(DashboardResponse.builder().build());

        adminDashboardController.getDashboard();

        verify(service, times(1)).getDashboard();
    }

    @Test
    void getDashboard_bodyNotNull() {
        when(service.getDashboard())
                .thenReturn(DashboardResponse.builder().build());

        ResponseEntity<DashboardResponse> result =
                adminDashboardController.getDashboard();

        assertNotNull(result.getBody());
    }
}