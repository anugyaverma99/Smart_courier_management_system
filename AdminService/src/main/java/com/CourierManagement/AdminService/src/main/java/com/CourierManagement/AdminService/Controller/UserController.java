package com.CourierManagement.AdminService.Controller;

import com.CourierManagement.AdminService.Client.UserClient;
import com.CourierManagement.AdminService.Dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Admin user management APIs")
public class UserController {

    private final UserClient userClient;

    // get all registered users via feign
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userClient.getAllUsers());
    }

    // deactivate a user account
    @PutMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userClient.deactivateUser(userId));
    }

    // activate a user account
    @PutMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user")
    public ResponseEntity<UserDto> activateUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userClient.activateUser(userId));
    }
}