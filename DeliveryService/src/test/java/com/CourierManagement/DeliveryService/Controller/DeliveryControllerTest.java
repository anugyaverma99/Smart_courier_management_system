package com.CourierManagement.DeliveryService.Controller;

import com.CourierManagement.DeliveryService.Dto.*;
import com.CourierManagement.DeliveryService.Service.DeliveryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService service;

    @Autowired
    private ObjectMapper objectMapper;

    // Sample delivery response
    private DeliveryResponse sampleResponse() {
        return DeliveryResponse.builder()
                .id(1L)
                .trackingNumber("TRK-1234ABCD")
                .customerId("cust123")
                .senderAddress(AddressDto.builder().name("Sender").build())
                .receiverAddress(AddressDto.builder().name("Receiver").build())
                .packageDetails(PackageDto.builder().description("Sample Package").build())
                .status(null)
                .charge(100.0)
                .pickupScheduledAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Sample create request
    private CreateDeliveryRequest sampleCreateRequest() {
        return CreateDeliveryRequest.builder()
                .customerId("cust123")
                .senderAddress(AddressDto.builder().name("Sender").build())
                .receiverAddress(AddressDto.builder().name("Receiver").build())
                .packageDetails(PackageDto.builder().description("Sample Package").build())
                .pickupScheduledAt(LocalDateTime.now())
                .build();
    }

    // Sample update status request
    private UpdateStatusRequest sampleStatusRequest() {
        return UpdateStatusRequest.builder()
                .status(null)
                .build();
    }

    

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testGetMyDeliveries() throws Exception {
        List<DeliveryResponse> deliveries = List.of(sampleResponse());
        Mockito.when(service.getMyDeliveries(anyString())).thenReturn(deliveries);

        mockMvc.perform(get("/deliveries/my")
                        .param("customerId", "cust123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testGetByIdAsCustomer() throws Exception {
        DeliveryResponse response = sampleResponse();
        Mockito.when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetByIdAsAdmin() throws Exception {
        DeliveryResponse response = sampleResponse();
        Mockito.when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testGetByTrackingNumber() throws Exception {
        DeliveryResponse response = sampleResponse();
        Mockito.when(service.getByTrackingNumber("TRK-1234ABCD")).thenReturn(response);

        mockMvc.perform(get("/deliveries/track/TRK-1234ABCD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackingNumber").value("TRK-1234ABCD"));
    }

   
    @Test
    @WithMockUser(roles = {"CUSTOMER"})
    void testDoesDeliveryExist() throws Exception {
        DeliveryResponse response = sampleResponse();
        Mockito.when(service.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/deliveries/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAllDeliveries() throws Exception {
        List<DeliveryResponse> deliveries = List.of(sampleResponse());
        Mockito.when(service.getAllDeliveries()).thenReturn(deliveries);

        mockMvc.perform(get("/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}