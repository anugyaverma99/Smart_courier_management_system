// service/AuthService.java
package com.CourierManagement.AuthService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Dto.LoginRequest;
import com.CourierManagement.AuthService.Dto.SignupRequest;
import com.CourierManagement.AuthService.Entity.User;
import com.CourierManagement.AuthService.Exception.AuthServiceException;
import com.CourierManagement.AuthService.Repository.UserRepository;
import com.CourierManagement.AuthService.Security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    
    public AuthResponse signup(SignupRequest request) {

        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthServiceException(
                    "Email already registered: " + request.getEmail());
        }

        
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null
                        ? request.getRole()
                        : com.CourierManagement.AuthService.Entity.Role.CUSTOMER)
                .build();

        User saved = userRepository.save(user);

        
        String token = jwtUtil.generateToken(
                saved.getEmail(), saved.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .userId(saved.getId())
                .build();
    }

    
    public AuthResponse login(LoginRequest request) {

        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthServiceException(
                        "Invalid email or password"));

       
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthServiceException("Invalid email or password");
        }

        
        if (!user.isActive()) {
            throw new AuthServiceException("Account is deactivated");
        }

        
        String token = jwtUtil.generateToken(
                user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }
}