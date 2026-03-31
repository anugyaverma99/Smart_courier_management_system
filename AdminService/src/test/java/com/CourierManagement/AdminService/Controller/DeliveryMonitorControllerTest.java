package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.DeliveryMonitorRequest;
import com.CourierManagement.AdminService.Dto.DeliveryMonitorResponse;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Service.DeliveryMonitorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryMonitorControllerTest {

    @Mock
    private DeliveryMonitorService service;

    @InjectMocks
    private DeliveryMonitorController deliveryMonitorController;

    @Test
    void syncDelivery_success() {
        DeliveryMonitorRequest request = new DeliveryMonitorRequest();
        DeliveryMonitorResponse mockResponse = new DeliveryMonitorResponse();

        when(service.syncDelivery(any(DeliveryMonitorRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<DeliveryMonitorResponse> result =
                deliveryMonitorController.syncDelivery(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(service, times(1)).syncDelivery(any(DeliveryMonitorRequest.class));
    }

    @Test
    void getAllDeliveries_success() {
        DeliveryMonitorResponse response1 = new DeliveryMonitorResponse();
        DeliveryMonitorResponse response2 = new DeliveryMonitorResponse();

        when(service.getAllDeliveries()).thenReturn(List.of(response1, response2));

        ResponseEntity<List<DeliveryMonitorResponse>> result =
                deliveryMonitorController.getAllDeliveries();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(service, times(1)).getAllDeliveries();
    }

    @Test
    void getByDeliveryId_success() {
        DeliveryMonitorResponse mockResponse = new DeliveryMonitorResponse();

        when(service.getByDeliveryId("DEL-001")).thenReturn(mockResponse);

        ResponseEntity<DeliveryMonitorResponse> result =
                deliveryMonitorController.getByDeliveryId("DEL-001");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).getByDeliveryId("DEL-001");
    }

    @Test
    void getByStatus_success() {
        DeliveryMonitorResponse mockResponse = new DeliveryMonitorResponse();

        when(service.getByStatus(DeliveryStatus.IN_TRANSIT))
                .thenReturn(List.of(mockResponse));

        ResponseEntity<List<DeliveryMonitorResponse>> result =
                deliveryMonitorController.getByStatus(DeliveryStatus.IN_TRANSIT);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(service, times(1)).getByStatus(DeliveryStatus.IN_TRANSIT);
    }

    @Test
    void getByHub_success() {
        DeliveryMonitorResponse mockResponse = new DeliveryMonitorResponse();

        when(service.getByHub("Mumbai Hub")).thenReturn(List.of(mockResponse));

        ResponseEntity<List<DeliveryMonitorResponse>> result =
                deliveryMonitorController.getByHub("Mumbai Hub");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(service, times(1)).getByHub("Mumbai Hub");
    }

    @Test
    void updateStatus_success() {
        DeliveryMonitorResponse mockResponse = new DeliveryMonitorResponse();

        when(service.updateStatus("DEL-001", DeliveryStatus.DELIVERED))
                .thenReturn(mockResponse);

        ResponseEntity<DeliveryMonitorResponse> result =
                deliveryMonitorController.updateStatus("DEL-001", "DELIVERED");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1))
                .updateStatus("DEL-001", DeliveryStatus.DELIVERED);
    }

    @Test
    void updateStatus_invalidStatus_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> deliveryMonitorController.updateStatus("DEL-001", "INVALID_STATUS"));
    }

    @Test
    void getAllDeliveries_emptyList() {
        when(service.getAllDeliveries()).thenReturn(List.of());

        ResponseEntity<List<DeliveryMonitorResponse>> result =
                deliveryMonitorController.getAllDeliveries();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, result.getBody().size());
    }
}
