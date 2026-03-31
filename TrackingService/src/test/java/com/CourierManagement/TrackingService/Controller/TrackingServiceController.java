package com.CourierManagement.TrackingService.Controller;

import com.CourierManagement.TrackingService.Dto.TrackingEventRequest;
import com.CourierManagement.TrackingService.Dto.TrackingEventResponse;
import com.CourierManagement.TrackingService.Entity.TrackingStatus;
import com.CourierManagement.TrackingService.Service.TrackingEventService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackingEventControllerTest {

    @InjectMocks
    private TrackingEventController controller;

    @Mock
    private TrackingEventService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddEvent_success() {
        // Use builder or constructor as per your DTO
        TrackingEventRequest request = TrackingEventRequest.builder()
                .trackingNumber("TRK-1234")
                .status(TrackingStatus.IN_TRANSIT)  // enum, not String
                .location("Hub A")
                .build();

        TrackingEventResponse response = TrackingEventResponse.builder()
                .trackingNumber("TRK-1234")
                .status(TrackingStatus.IN_TRANSIT)
                .location("Hub A")
                .build();

        when(service.addEvent(any(TrackingEventRequest.class))).thenReturn(response);

        ResponseEntity<TrackingEventResponse> result = controller.addEvent(request);

        assertNotNull(result);
        assertEquals(201, result.getStatusCodeValue());
        assertEquals(request.getTrackingNumber(), result.getBody().getTrackingNumber());
        verify(service, times(1)).addEvent(request);
    }

    @Test
    void testGetTimeline_success() {
        String trackingNumber = "TRK-1234";

        TrackingEventResponse event1 = TrackingEventResponse.builder()
                .trackingNumber(trackingNumber)
                .status(TrackingStatus.BOOKED)
                .location("Hub A")
                .build();

        TrackingEventResponse event2 = TrackingEventResponse.builder()
                .trackingNumber(trackingNumber)
                .status(TrackingStatus.IN_TRANSIT)
                .location("Hub B")
                .build();

        when(service.getTimeline(trackingNumber)).thenReturn(List.of(event1, event2));

        ResponseEntity<List<TrackingEventResponse>> result = controller.getTimeline(trackingNumber);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());
        verify(service, times(1)).getTimeline(trackingNumber);
    }

    @Test
    void testGetTotalEventCount_success() {
        when(service.getTotalEventCount()).thenReturn(10L);

        ResponseEntity<Long> result = controller.getTotalEventCount();

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(10L, result.getBody());
        verify(service, times(1)).getTotalEventCount();
    }

    @Test
    void testGetLatest_success() {
        String trackingNumber = "TRK-1234";

        TrackingEventResponse response = TrackingEventResponse.builder()
                .trackingNumber(trackingNumber)
                .status(TrackingStatus.DELIVERED)
                .location("Customer Address")
                .build();

        when(service.getLatestStatus(trackingNumber)).thenReturn(response);

        ResponseEntity<TrackingEventResponse> result = controller.getLatest(trackingNumber);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(TrackingStatus.DELIVERED, result.getBody().getStatus());
        verify(service, times(1)).getLatestStatus(trackingNumber);
    }
}