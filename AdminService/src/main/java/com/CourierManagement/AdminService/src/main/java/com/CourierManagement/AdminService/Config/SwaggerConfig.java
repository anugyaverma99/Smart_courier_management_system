package com.CourierManagement.AdminService.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SmartCourier API")
                        .description("SmartCourier Delivery Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SmartCourier Team")
                                .email("admin@smartcourier.com")))
                .servers(List.of(
                	    new Server()
                	        .url("http://localhost:8080/gateway")  // ← add /gateway
                	        .description("Via Gateway")
                	))
                // Adds JWT bearer token input to Swagger UI
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")));
    }
}
