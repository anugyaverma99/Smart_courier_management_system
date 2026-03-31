package com.CourierManagement.AdminService.Service;

import com.CourierManagement.AdminService.Dto.ExceptionRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResolveRequest;
import com.CourierManagement.AdminService.Dto.ExceptionResponse;
import com.CourierManagement.AdminService.Entity.DeliveryException;
import com.CourierManagement.AdminService.Entity.DeliveryStatus;
import com.CourierManagement.AdminService.Entity.ExceptionStatus;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.DeliveryExceptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlingServiceTest {

    @Mock
    private DeliveryExceptionRepository exceptionRepository;

    @InjectMocks
    private ExceptionHandlingService exceptionService;

    private DeliveryException testException;

    @BeforeEach
    void setUp() {
        testException = DeliveryException.builder()
                .id(1L).deliveryId("1")
                .trackingNumber("TRK-TEST001")
                .exceptionStatus(DeliveryStatus.DELAYED)
                .resolutionStatus(ExceptionStatus.OPEN)
                .reason("Weather delay")
                .raisedAt(LocalDateTime.now()).build();
    }

    @Test
    void raiseException_success() {
        when(exceptionRepository.save(any(DeliveryException.class)))
                .thenReturn(testException);

        ExceptionRequest request = ExceptionRequest.builder()
                .deliveryId("1").trackingNumber("TRK-TEST001")
                .exceptionStatus(DeliveryStatus.DELAYED)
                .reason("Weather delay").build();

        ExceptionResponse response = exceptionService.raiseException(request);

        assertNotNull(response);
        assertEquals("1", response.getDeliveryId());
        assertEquals(ExceptionStatus.OPEN, response.getResolutionStatus());
    }

    @Test
    void getOpenExceptions_success() {
        when(exceptionRepository.findByResolutionStatus(ExceptionStatus.OPEN))
                .thenReturn(List.of(testException));

        List<ExceptionResponse> responses = exceptionService.getOpenExceptions();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void resolveException_success() {
        when(exceptionRepository.findById(1L))
                .thenReturn(Optional.of(testException));
        when(exceptionRepository.save(any(DeliveryException.class)))
                .thenReturn(testException);

        ExceptionResolveRequest request = ExceptionResolveRequest.builder()
                .remarks("Issue resolved")
                .resolvedBy("admin1").build();

        ExceptionResponse response = exceptionService.resolveException(1L, request);

        assertNotNull(response);
        verify(exceptionRepository).save(any(DeliveryException.class));
    }

    @Test
    void resolveException_notFound_throwsException() {
        when(exceptionRepository.findById(99L))
                .thenReturn(Optional.empty());

        ExceptionResolveRequest request = new ExceptionResolveRequest();  // ← move outside

        assertThrows(AdminServiceException.class,
                () -> exceptionService.resolveException(99L, request));  // ← single invocation
    }

    @Test
    void resolveException_alreadyResolved_throwsException() {
        testException.setResolutionStatus(ExceptionStatus.RESOLVED);
        when(exceptionRepository.findById(1L))
                .thenReturn(Optional.of(testException));

        ExceptionResolveRequest request = new ExceptionResolveRequest();  // ← move outside

        assertThrows(AdminServiceException.class,
                () -> exceptionService.resolveException(1L, request));  // ← single invocation
    }
}