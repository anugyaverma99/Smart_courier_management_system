package com.CourierManagement.AdminService.Service;

import com.CourierManagement.AdminService.Dto.DashboardResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;
import com.CourierManagement.AdminService.Repository.DeliveryExceptionRepository;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import com.CourierManagement.AdminService.Repository.HubRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {

    @Mock
    private DeliveryMonitorRepository deliveryMonitorRepository;

    @Mock
    private DeliveryExceptionRepository exceptionRepository;

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private AdminDashboardService adminDashboardService;

    @Test
    void shouldReturnDashboardWithCorrectValues() {
        when(deliveryMonitorRepository.count()).thenReturn(100L);
        when(deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.DELIVERED)).thenReturn(40L);
        when(deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.IN_TRANSIT)).thenReturn(30L);
        when(deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.OUT_FOR_DELIVERY)).thenReturn(20L);
        when(exceptionRepository.countByResolutionStatus(ExceptionStatus.OPEN)).thenReturn(5L);
        when(hubRepository.countByActiveTrue()).thenReturn(10L);

        DashboardResponse response = adminDashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(100L, response.getTotalDeliveries());
        assertEquals(40L, response.getDeliveredToday());
        assertEquals(30L, response.getInTransit());
        assertEquals(20L, response.getOutForDelivery());
        assertEquals(5L, response.getExceptions());
        assertEquals(10L, response.getActiveHubs());
    }

    @Test
    void shouldReturnZeroValuesWhenNoData() {
        when(deliveryMonitorRepository.count()).thenReturn(0L);
        when(deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.DELIVERED)).thenReturn(0L);
        when(deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.IN_TRANSIT)).thenReturn(0L);
        when(deliveryMonitorRepository.countByCurrentStatus(DeliveryStatus.OUT_FOR_DELIVERY)).thenReturn(0L);
        when(exceptionRepository.countByResolutionStatus(ExceptionStatus.OPEN)).thenReturn(0L);
        when(hubRepository.countByActiveTrue()).thenReturn(0L);

        DashboardResponse response = adminDashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(0L, response.getTotalDeliveries());
        assertEquals(0L, response.getDeliveredToday());
        assertEquals(0L, response.getInTransit());
        assertEquals(0L, response.getOutForDelivery());
        assertEquals(0L, response.getExceptions());
        assertEquals(0L, response.getActiveHubs());
    }

    @Test
    void shouldCallAllRepositoryMethods() {
        when(deliveryMonitorRepository.count()).thenReturn(50L);
        when(deliveryMonitorRepository.countByCurrentStatus(any(DeliveryStatus.class))).thenReturn(10L);
        when(exceptionRepository.countByResolutionStatus(any(ExceptionStatus.class))).thenReturn(2L);
        when(hubRepository.countByActiveTrue()).thenReturn(5L);

        adminDashboardService.getDashboard();

        verify(deliveryMonitorRepository, times(1)).count();
        verify(deliveryMonitorRepository, times(1)).countByCurrentStatus(DeliveryStatus.DELIVERED);
        verify(deliveryMonitorRepository, times(1)).countByCurrentStatus(DeliveryStatus.IN_TRANSIT);
        verify(deliveryMonitorRepository, times(1)).countByCurrentStatus(DeliveryStatus.OUT_FOR_DELIVERY);
        verify(exceptionRepository, times(1)).countByResolutionStatus(ExceptionStatus.OPEN);
        verify(hubRepository, times(1)).countByActiveTrue();
    }

    @Test
    void shouldReturnDashboardResponseNotNull() {
        when(deliveryMonitorRepository.count()).thenReturn(10L);
        when(deliveryMonitorRepository.countByCurrentStatus(any(DeliveryStatus.class))).thenReturn(3L);
        when(exceptionRepository.countByResolutionStatus(any(ExceptionStatus.class))).thenReturn(1L);
        when(hubRepository.countByActiveTrue()).thenReturn(2L);

        DashboardResponse response = adminDashboardService.getDashboard();

        assertNotNull(response);
    }
}