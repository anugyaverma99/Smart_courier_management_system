package com.CourierManagement.AuthService.Dto;

import com.CourierManagement.AuthService.Entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Role role;
}