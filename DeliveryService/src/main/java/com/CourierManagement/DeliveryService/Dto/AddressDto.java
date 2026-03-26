package com.CourierManagement.DeliveryService.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor 
@Builder
public class AddressDto {
	@NotBlank(message="name is required")
    private String name;
	
	 @NotBlank(message = "Phone is required")
	  @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
	private String phone;
	 
	 @Email(message = "Invalid email format") 
    private String email;
	 
	 @NotBlank(message = "Address line is required")
    private String addressLine;
	
	 @NotBlank(message = "City is required")
    private String city;
	 
	 @NotBlank(message = "State is required")
    private String state;
	 
	 @NotBlank(message = "Pincode is required")
	    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be 6 digits")
	   
    private String zipCode;
	 
	 @NotBlank(message = "Country is required")
    private String country;
}
