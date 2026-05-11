package com.CourierManagement.AuthService.Config;

import com.CourierManagement.AuthService.Entity.User;
import com.CourierManagement.AuthService.Entity.Role;
import com.CourierManagement.AuthService.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
//    CommandLineRunner means: runs automatically 
//    after Spring Boot starts executes only once at startup
// used for setup tasks
    CommandLineRunner init(UserRepository repo, PasswordEncoder encoder) {
        return args -> {

            // create admin only if not exists
            if (repo.count() == 0) {

                User admin = User.builder()
                        .email("admin@courier.com")
                        .password(encoder.encode("admin123"))
                        .fullName("System Admin")
                        .role(Role.ADMIN)
                        .active(true)
                        .build();

                repo.save(admin);
            }
        };
    }
}