package com.CourierManagement.AuthService.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Dto.LoginRequest;
import com.CourierManagement.AuthService.Dto.SignupRequest;
import com.CourierManagement.AuthService.Entity.Role;
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


    // create a new customer account
    public AuthResponse signup(SignupRequest request) {

        // check if email is already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthServiceException(
                    "Email already registered: " + request.getEmail());
        }

        // prevent admin creation via public signup
        if (request.getRole() == Role.ADMIN) {
            throw new AuthServiceException(
                    "Admin cannot be created via public signup");
        }

        // build new user object
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.CUSTOMER)
                .build();

        // save to database
        User saved = userRepository.save(user);

        // generate jwt token
        String token = jwtUtil.generateToken(
                saved.getEmail(),
                saved.getRole().name()
        );

        // return token and user details
        return AuthResponse.builder()
                .token(token)
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .userId(saved.getId())
                .build();
    }

   
    // authenticate existing user
    public AuthResponse login(LoginRequest request) {

        // find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AuthServiceException("Invalid email or password"));

        // validate password
        if (user.getPassword() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthServiceException("Invalid email or password");
        }

        // check if account is active
        if (!user.isActive()) {
            throw new AuthServiceException("Account is deactivated");
        }

        // generate jwt token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // return token and user details
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }

     public AuthResponse createAdmin(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthServiceException("Email already exists");
        }

        User admin = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.ADMIN)
                .build();

        User saved = userRepository.save(admin);

        String token = jwtUtil.generateToken(
                saved.getEmail(),
                saved.getRole().name()
        );

        return AuthResponse.builder()
                .token(token)
                .email(saved.getEmail())
                .fullName(saved.getFullName())
                .role(saved.getRole().name())
                .userId(saved.getId())
                .build();
    }
}