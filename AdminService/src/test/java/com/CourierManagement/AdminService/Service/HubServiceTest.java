package com.CourierManagement.AdminService.Service;

import com.CourierManagement.AdminService.Dto.HubRequest;
import com.CourierManagement.AdminService.Dto.HubResponse;
import com.CourierManagement.AdminService.Entity.Hub;
import com.CourierManagement.AdminService.Exception.AdminServiceException;
import com.CourierManagement.AdminService.Repository.HubRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private HubService hubService;

    private Hub testHub;

    @BeforeEach
    void setUp() {
        testHub = Hub.builder()
                .id(1L).name("Mumbai Hub")
                .city("Mumbai").state("Maharashtra")
                .pincode("400001")
                .contactNumber("9876543210")
                .active(true).build();
    }

    @Test
    void createHub_success() {
        when(hubRepository.save(any(Hub.class))).thenReturn(testHub);

        HubRequest request = HubRequest.builder()
                .name("Mumbai Hub").city("Mumbai")
                .state("Maharashtra").pincode("400001")
                .contactNumber("9876543210").build();

        HubResponse response = hubService.createHub(request);

        assertNotNull(response);
        assertEquals("Mumbai Hub", response.getName());
        assertTrue(response.isActive());
    }

    @Test
    void getActiveHubs_success() {
        when(hubRepository.findByActiveTrue())
                .thenReturn(List.of(testHub));

        List<HubResponse> responses = hubService.getActiveHubs();

        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
    }

    @Test
    void deactivateHub_success() {
        when(hubRepository.findById(1L))
                .thenReturn(Optional.of(testHub));
        when(hubRepository.save(any(Hub.class))).thenReturn(testHub);

        HubResponse response = hubService.deactivateHub(1L);

        assertNotNull(response);
        verify(hubRepository).save(any(Hub.class));
    }

    @Test
    void deactivateHub_notFound_throwsException() {
        when(hubRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(AdminServiceException.class,
                () -> hubService.deactivateHub(99L));
    }
}