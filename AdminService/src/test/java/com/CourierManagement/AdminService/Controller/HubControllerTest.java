package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.HubRequest;
import com.CourierManagement.AdminService.Dto.HubResponse;
import com.CourierManagement.AdminService.Service.HubService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HubControllerTest {

    @Mock
    private HubService service;

    @InjectMocks
    private HubController hubController;

    @Test
    void createHub_success() {
        HubRequest request = new HubRequest();
        HubResponse mockResponse = new HubResponse();

        when(service.createHub(any(HubRequest.class))).thenReturn(mockResponse);

        ResponseEntity<HubResponse> result = hubController.createHub(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(service, times(1)).createHub(any(HubRequest.class));
    }

    @Test
    void getActiveHubs_success() {
        when(service.getActiveHubs())
                .thenReturn(List.of(new HubResponse(), new HubResponse()));

        ResponseEntity<List<HubResponse>> result = hubController.getActiveHubs();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(service, times(1)).getActiveHubs();
    }

    @Test
    void getActiveHubs_emptyList() {
        when(service.getActiveHubs()).thenReturn(List.of());

        ResponseEntity<List<HubResponse>> result = hubController.getActiveHubs();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, result.getBody().size());
    }

    @Test
    void getAllHubs_success() {
        when(service.getAllHubs())
                .thenReturn(List.of(new HubResponse()));

        ResponseEntity<List<HubResponse>> result = hubController.getAllHubs();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(service, times(1)).getAllHubs();
    }

    @Test
    void deactivateHub_success() {
        HubResponse mockResponse = new HubResponse();
        when(service.deactivateHub(1L)).thenReturn(mockResponse);

        ResponseEntity<HubResponse> result = hubController.deactivateHub(1L);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1)).deactivateHub(1L);
    }
}
