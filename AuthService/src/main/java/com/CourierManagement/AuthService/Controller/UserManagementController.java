package com.CourierManagement.AuthService.Controller;

import com.CourierManagement.AuthService.Dto.AuthResponse;
import com.CourierManagement.AuthService.Entity.User;
import com.CourierManagement.AuthService.Exception.AuthServiceException;
import com.CourierManagement.AuthService.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Internal user APIs for Admin Service")
public class UserManagementController {

    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<AuthResponse>> getAllUsers() {
        List<AuthResponse> users = userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<AuthResponse> deactivateUser(@PathVariable Long userId) {
        User user = findUser(userId);
        user.setActive(false);
        return ResponseEntity.ok(toResponse(userRepository.save(user)));
    }

    @PutMapping("/{userId}/activate")
    @Operation(summary = "Activate user")
    public ResponseEntity<AuthResponse> activateUser(@PathVariable Long userId) {
        User user = findUser(userId);
        user.setActive(true);
        return ResponseEntity.ok(toResponse(userRepository.save(user)));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthServiceException("User not found: " + userId));
    }

    private AuthResponse toResponse(User u) {
        return AuthResponse.builder()
                .userId(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .role(u.getRole().name())
                .build();
    }
}
