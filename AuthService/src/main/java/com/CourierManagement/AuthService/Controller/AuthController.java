package com.CourierManagement.AuthService.Controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Dto.LoginRequest;
import com.CourierManagement.AuthService.Dto.SignupRequest;
import com.CourierManagement.AuthService.Entity.Role;
import com.CourierManagement.AuthService.Service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication APIs — signup and login")
public class AuthController {

    private final AuthService authService;

    // handle user registration
    @PostMapping("/signup")
    @Operation(summary = "Register new user", description = "Creates a new CUSTOMER or ADMIN account")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest request) {
    	System.out.println("SIGNUP API HIT");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.signup(request));
    }
    
    // handle admin creation by an existing admin
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> createAdmin(
            @RequestBody SignupRequest request) {
        request.setRole(Role.ADMIN);
        return ResponseEntity.ok(authService.createAdmin(request));
    }
   
    // handle user login and token generation
    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticate and get JWT token")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
}