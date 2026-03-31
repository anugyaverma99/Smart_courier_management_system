package com.CourierManagement.DeliveryService.Service;

import com.CourierManagement.DeliveryService.Client.AdminClient;
import com.CourierManagement.DeliveryService.Dto.*;
import com.CourierManagement.DeliveryService.Entity.*;
import com.CourierManagement.DeliveryService.Exception.DeliveryServiceException;
import com.CourierManagement.DeliveryService.Repository.DeliveryRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository repository;

    @Mock
    private AdminClient adminClient; // Mock external dependency

    @InjectMocks
    private DeliveryService deliveryService;

    private CreateDeliveryRequest createRequest;
    private Delivery testDelivery;
    private LocalDateTime fixedTime;

    @BeforeEach
    void setUp() {
        fixedTime = LocalDateTime.of(2026, 3, 29, 18, 0);

        Address sender = Address.builder()
                .name("Kanha").phone("9876543210")
                .addressLine("123 MG Road").city("Mumbai")
                .state("Maharashtra").zipCode("400001")
                .country("India").build();

        Address receiver = Address.builder()
                .name("Rahul").phone("9123456780")
                .addressLine("456 Brigade Road").city("Bangalore")
                .state("Karnataka").zipCode("560001")
                .country("India").build();

        PackageDetails pkg = PackageDetails.builder()
                .description("Laptop").weightKg(2.5)
                .lengthCm(40).widthCm(30).heightCm(10)
                .serviceType("express").declaredValue(50000)
                .build();

        testDelivery = Delivery.builder()
                .id(1L)
                .trackingNumber("TRK-TEST001")
                .customerId("1")
                .senderAddress(sender)
                .receiverAddress(receiver)
                .packageDetails(pkg)
                .status(DeliveryStatus.DRAFT)
                .charge(200.0)
                .createdAt(fixedTime)
                .updatedAt(fixedTime)
                .build();

        AddressDto senderDTO = AddressDto.builder()
                .name("Kanha").phone("9876543210")
                .email("kanha@gmail.com")
                .addressLine("123 MG Road").city("Mumbai")
                .state("Maharashtra").zipCode("400001")
                .country("India").build();

        AddressDto receiverDTO = AddressDto.builder()
                .name("Rahul").phone("9123456780")
                .email("rahul@gmail.com")
                .addressLine("456 Brigade Road").city("Bangalore")
                .state("Karnataka").zipCode("560001")
                .country("India").build();

        PackageDto packageDTO = PackageDto.builder()
                .description("Laptop").weightKg(2.5)
                .lengthCm(40).widthCm(30).heightCm(10)
                .serviceType("express").declaredValue(50000)
                .build();

        createRequest = CreateDeliveryRequest.builder()
                .customerId("1")
                .senderAddress(senderDTO)
                .receiverAddress(receiverDTO)
                .packageDetails(packageDTO)
                .build();
    }

    @Test
    void createDelivery_success() {
        // Mock repository.save() to simulate DB save
        when(repository.save(any(Delivery.class)))
                .thenAnswer(invocation -> {
                    Delivery arg = invocation.getArgument(0);
                    arg.setId(1L);
                    arg.setTrackingNumber("TRK-TEST001");
                    arg.setCreatedAt(fixedTime);
                    arg.setUpdatedAt(fixedTime);
                    return arg;
                });

        // Mock void methods of AdminClient
        doNothing().when(adminClient).updateMonitorStatus(anyString(), anyString());
        doNothing().when(adminClient).syncDelivery(any(DeliveryMonitorRequest.class));

        DeliveryResponse response = deliveryService.createDelivery(createRequest);

        assertNotNull(response);
        assertEquals("TRK-TEST001", response.getTrackingNumber());
        assertEquals("1", response.getCustomerId());
        assertEquals(DeliveryStatus.DRAFT, response.getStatus());

        verify(repository, times(1)).save(any(Delivery.class));
        verify(adminClient, times(1)).syncDelivery(any(DeliveryMonitorRequest.class));
    }

    @Test
    void getById_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(testDelivery));

        DeliveryResponse response = deliveryService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TRK-TEST001", response.getTrackingNumber());
    }

    @Test
    void getById_notFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DeliveryServiceException.class, () -> deliveryService.getById(99L));
    }

    @Test
    void getMyDeliveries_success() {
        when(repository.findByCustomerIdOrderByCreatedAtDesc("1"))
                .thenReturn(List.of(testDelivery));

        List<DeliveryResponse> responses = deliveryService.getMyDeliveries("1");

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals("TRK-TEST001", responses.get(0).getTrackingNumber());
    }

    @Test
    void getMyDeliveries_notFound_throwsException() {
        when(repository.findByCustomerIdOrderByCreatedAtDesc("99"))
                .thenReturn(List.of());

        assertThrows(DeliveryServiceException.class, () -> deliveryService.getMyDeliveries("99"));
    }

    @Test
    void updateStatus_success() {
        when(repository.findById(1L)).thenReturn(Optional.of(testDelivery));
        when(repository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(adminClient).updateMonitorStatus(anyString(), anyString());

        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(DeliveryStatus.BOOKED)
                .updatedBy("admin1")
                .remarks("Confirmed")
                .build();

        DeliveryResponse response = deliveryService.updateStatus(1L, request);

        assertNotNull(response);
        assertEquals(DeliveryStatus.BOOKED, response.getStatus());
        verify(repository, times(1)).save(any(Delivery.class));
        verify(adminClient, times(1)).updateMonitorStatus(anyString(), anyString());
    }

    @Test
    void updateStatus_invalidTransition_throwsException() {
        testDelivery.setStatus(DeliveryStatus.DELIVERED);
        when(repository.findById(1L)).thenReturn(Optional.of(testDelivery));

        UpdateStatusRequest request = UpdateStatusRequest.builder()
                .status(DeliveryStatus.DRAFT)
                .build();

        assertThrows(DeliveryServiceException.class, () -> deliveryService.updateStatus(1L, request));
    }
}