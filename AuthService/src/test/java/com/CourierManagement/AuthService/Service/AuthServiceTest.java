package com.CourierManagement.AuthService.Service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Dto.LoginRequest;
import com.CourierManagement.AuthService.Dto.SignupRequest;
import com.CourierManagement.AuthService.Entity.Role;
import com.CourierManagement.AuthService.Entity.User;
import com.CourierManagement.AuthService.Exception.AuthServiceException;
import com.CourierManagement.AuthService.Repository.UserRepository;
import com.CourierManagement.AuthService.Security.JwtUtil;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    private User testUser;
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).fullName("Sandeep Chavan")
                .email("sandeep@gmail.com")
                .password("hashedPassword")
                .role(Role.CUSTOMER).active(true).build();

        signupRequest = SignupRequest.builder()
                .fullName("Sandeep Chavan")
                .email("sandeep@gmail.com")
                .password("password123")
                .phone("9876543210")
                .role(Role.CUSTOMER).build();

        loginRequest = LoginRequest.builder()
                .email("sandeep@gmail.com")
                .password("password123").build();
    }

    @Test
    void signup_success() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.signup(signupRequest);

        assertNotNull(response);
        assertEquals("sandeep@gmail.com", response.getEmail());
        assertEquals("mockToken", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_duplicateEmail_throwsException() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(AuthServiceException.class,
                () -> authService.signup(signupRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void login_invalidEmail_throwsException() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThrows(AuthServiceException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_wrongPassword_throwsException() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(AuthServiceException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_inactiveAccount_throwsException() {
        testUser.setActive(false);
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertThrows(AuthServiceException.class,
                () -> authService.login(loginRequest));
    }
}
