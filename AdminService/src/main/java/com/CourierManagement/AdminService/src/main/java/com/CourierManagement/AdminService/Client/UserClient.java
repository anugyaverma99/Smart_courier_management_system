
package com.CourierManagement.AdminService.Client;

import com.CourierManagement.AdminService.Config.FeignConfig;
import com.CourierManagement.AdminService.Dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "auth-service",configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/auth/users")
    List<UserDto> getAllUsers();

    @PutMapping("/auth/users/{userId}/deactivate")
    UserDto deactivateUser(@PathVariable Long userId);

    @PutMapping("/auth/users/{userId}/activate")
    UserDto activateUser(@PathVariable Long userId);
}