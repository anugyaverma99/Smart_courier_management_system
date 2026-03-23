package com.CourierManagement.AuthService.Dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}