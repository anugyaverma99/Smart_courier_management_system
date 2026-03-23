package com.CourierManagement.AuthService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Dto.LoginRequest;
import com.CourierManagement.AuthService.Dto.SignupRequest;
import com.CourierManagement.AuthService.Entity.Role;
import com.CourierManagement.AuthService.Entity.User;
import com.CourierManagement.AuthService.Repository.UserRepository;
import com.CourierManagement.AuthService.Security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String signup(SignupRequest request) {
    	if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    	    throw new RuntimeException("Email already exists");
    	}

        User user = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepository.save(user);

        return "User Registered Successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getRole().name());
    }
}