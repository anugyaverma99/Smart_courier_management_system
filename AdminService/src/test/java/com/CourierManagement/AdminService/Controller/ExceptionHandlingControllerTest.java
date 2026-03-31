package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Dto.ExceptionRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResolveRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResponse;
import com.CourierManagement.AdminService.Service.ExceptionHandlingService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingControllerTest {

    @Mock
    private ExceptionHandlingService service;

    @InjectMocks
    private ExceptionHandlingController exceptionHandlingController;

    @Test
    void getOpenExceptions_success() {
        ExceptionResponse mockResponse = new ExceptionResponse();
        when(service.getOpenExceptions()).thenReturn(List.of(mockResponse));

        ResponseEntity<List<ExceptionResponse>> result =
                exceptionHandlingController.getOpenExceptions();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(service, times(1)).getOpenExceptions();
    }

    @Test
    void getOpenExceptions_emptyList() {
        when(service.getOpenExceptions()).thenReturn(List.of());

        ResponseEntity<List<ExceptionResponse>> result =
                exceptionHandlingController.getOpenExceptions();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(0, result.getBody().size());
    }

    @Test
    void getAllExceptions_success() {
        when(service.getAllExceptions())
                .thenReturn(List.of(new ExceptionResponse(), new ExceptionResponse()));

        ResponseEntity<List<ExceptionResponse>> result =
                exceptionHandlingController.getAllExceptions();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(service, times(1)).getAllExceptions();
    }

    @Test
    void getByDeliveryId_success() {
        when(service.getByDeliveryId("DEL-001"))
                .thenReturn(List.of(new ExceptionResponse()));

        ResponseEntity<List<ExceptionResponse>> result =
                exceptionHandlingController.getByDeliveryId("DEL-001");

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        verify(service, times(1)).getByDeliveryId("DEL-001");
    }

    @Test
    void raiseException_success() {
        ExceptionRequest request = new ExceptionRequest();
        ExceptionResponse mockResponse = new ExceptionResponse();

        when(service.raiseException(any(ExceptionRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<ExceptionResponse> result =
                exceptionHandlingController.raiseException(request);

        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(service, times(1)).raiseException(any(ExceptionRequest.class));
    }

    @Test
    void resolveException_success() {
        ExceptionResolveRequest request = new ExceptionResolveRequest();
        ExceptionResponse mockResponse = new ExceptionResponse();

        when(service.resolveException(eq(1L), any(ExceptionResolveRequest.class)))
                .thenReturn(mockResponse);

        ResponseEntity<ExceptionResponse> result =
                exceptionHandlingController.resolveException(1L, request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(service, times(1))
                .resolveException(eq(1L), any(ExceptionResolveRequest.class));
    }
}