package com.CourierManagement.AdminService.Service;


import com.CourierManagement.AdminService.Client.DeliveryClient;
import com.CourierManagement.AdminService.Client.TrackingClient;
import com.CourierManagement.AdminService.Dto.DeliveryDto;
import com.CourierManagement.AdminService.Dto.DeliveryMonitorRequest;
import com.CourierManagement.AdminService.Dto.DeliveryMonitorResponse;
import com.CourierManagement.AdminService.Dto.TrackingDto;
import com.CourierManagement.AdminService.Entity.DeliveryMonitor;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.DeliveryMonitorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryMonitorServiceTest {

    @InjectMocks
    private DeliveryMonitorService service;

    @Mock
    private DeliveryMonitorRepository repository;

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private TrackingClient trackingClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllDeliveries() {
        DeliveryMonitor monitor = DeliveryMonitor.builder()
                .id(1L)
                .deliveryId("D1")
                .trackingNumber("TRK-1")
                .customerName("Alice")
                .senderCity("CityA")
                .recieverCity("CityB")
                .currentStatus(DeliveryStatus.BOOKED)
                .assignedHub("Hub1")
                .lastUpdated(LocalDateTime.now())
                .build();

        when(repository.findAll()).thenReturn(List.of(monitor));

        List<DeliveryMonitorResponse> responses = service.getAllDeliveries();

        assertEquals(1, responses.size());
        assertEquals("D1", responses.get(0).getDeliveryId());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetByStatus() {
        DeliveryMonitor monitor = DeliveryMonitor.builder()
                .id(2L)
                .deliveryId("D2")
                .trackingNumber("TRK-2")
                .currentStatus(DeliveryStatus.DELIVERED)
                .build();

        when(repository.findByCurrentStatus(DeliveryStatus.DELIVERED)).thenReturn(List.of(monitor));

        List<DeliveryMonitorResponse> responses = service.getByStatus(DeliveryStatus.DELIVERED);

        assertEquals(1, responses.size());
        assertEquals(DeliveryStatus.DELIVERED, responses.get(0).getCurrentStatus());
        verify(repository, times(1)).findByCurrentStatus(DeliveryStatus.DELIVERED);
    }

    @Test
    void testGetByDeliveryId() {
        DeliveryMonitor monitor = DeliveryMonitor.builder()
                .id(3L)
                .deliveryId("D3")
                .trackingNumber("TRK-3")
                .customerName("Bob")
                .senderCity("CityX")
                .recieverCity("CityY")
                .currentStatus(DeliveryStatus.IN_TRANSIT)
                .assignedHub("Hub2")
                .lastUpdated(LocalDateTime.now())
                .build();

        DeliveryDto liveDelivery = DeliveryDto.builder()
                .senderName("Bob Sender")
                .receiverName("Bob Receiver")
                .build();

        TrackingDto latestTracking = TrackingDto.builder()
                .status("IN_TRANSIT")
                .location("Hub2")
                .build();

        when(repository.findByDeliveryId("D3")).thenReturn(Optional.of(monitor));
        when(deliveryClient.getDeliveryById("D3")).thenReturn(liveDelivery);
        when(trackingClient.getLatestStatus("TRK-3")).thenReturn(latestTracking);

        DeliveryMonitorResponse response = service.getByDeliveryId("D3");

        assertEquals("D3", response.getDeliveryId());
        assertEquals("Bob Sender", response.getLiveSenderName());
        assertEquals("Hub2", response.getLatestTrackingLocation());

        verify(repository, times(1)).findByDeliveryId("D3");
        verify(deliveryClient, times(1)).getDeliveryById("D3");
        verify(trackingClient, times(1)).getLatestStatus("TRK-3");
    }

    @Test
    void testUpdateStatus_success() {
        DeliveryMonitor monitor = DeliveryMonitor.builder()
                .deliveryId("D4")
                .currentStatus(DeliveryStatus.BOOKED)
                .build();

        when(repository.findByDeliveryId("D4")).thenReturn(Optional.of(monitor));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        DeliveryMonitorResponse response = service.updateStatus("D4", DeliveryStatus.DELIVERED);

        assertEquals(DeliveryStatus.DELIVERED, response.getCurrentStatus());
        verify(repository, times(1)).findByDeliveryId("D4");
        verify(repository, times(1)).save(monitor);
    }

    @Test
    void testUpdateStatus_notFound() {
        when(repository.findByDeliveryId("D5")).thenReturn(Optional.empty());

        AdminServiceException ex = assertThrows(AdminServiceException.class,
                () -> service.updateStatus("D5", DeliveryStatus.DELIVERED));

        assertTrue(ex.getMessage().contains("Delivery not found"));
        verify(repository, times(1)).findByDeliveryId("D5");
    }

    @Test
    void testSyncDelivery_newMonitor() {
        DeliveryMonitorRequest request = DeliveryMonitorRequest.builder()
                .deliveryId("D6")
                .trackingNumber("TRK-6")
                .customerName("Charlie")
                .senderCity("CityP")
                .receiverCity("CityQ")
                .currentStatus(DeliveryStatus.BOOKED)
                .assignedHub("Hub3")
                .build();

        when(repository.findByDeliveryId("D6")).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        DeliveryMonitorResponse response = service.syncDelivery(request);

        assertEquals("D6", response.getDeliveryId());
        assertEquals(DeliveryStatus.BOOKED, response.getCurrentStatus());
        verify(repository, times(1)).save(any());
    }
}
