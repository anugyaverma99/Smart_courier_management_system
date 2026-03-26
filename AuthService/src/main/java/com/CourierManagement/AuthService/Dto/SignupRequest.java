package com.CourierManagement.AuthService.Dto;

import com.CourierManagement.AuthService.Entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
	@NotBlank(message = "Full name is required")
    private String fullName;
	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
	@NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    
    private String password;
	
	 @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
	   
    private String phone;
    private Role role;
}