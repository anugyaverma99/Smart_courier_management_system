package com.CourierManagement.AuthService.Controller;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Dto.LoginRequest;
import com.CourierManagement.AuthService.Dto.SignupRequest;
import com.CourierManagement.AuthService.Entity.Role;
import com.CourierManagement.AuthService.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup() {
        SignupRequest request = SignupRequest.builder()
                .fullName("testuser")
                .email("test@example.com")
                .password("password123")
                .role(Role.CUSTOMER)
                .build();

        AuthResponse mockResponse = AuthResponse.builder()
                .userId(1L)
                .fullName("testuser")
                .email("test@example.com")
                .token("jwt-token")
                .build();

        when(authService.signup(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.signup(request);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getUserId()); // ✅ fixed
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService, times(1)).signup(request);
    }

    @Test
    void testLogin() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponse mockResponse = AuthResponse.builder()
                .userId(1L)
                .fullName("testuser")
                .email("test@example.com")
                .token("jwt-token")
                .build();

        when(authService.login(request)).thenReturn(mockResponse);

        ResponseEntity<AuthResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getUserId()); // ✅ fixed
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService, times(1)).login(request);
    }
}