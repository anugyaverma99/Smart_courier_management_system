package com.CourierManagement.AdminService.Dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean active;
}
