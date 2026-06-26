package com.example.library.config;

import com.example.library.entity.User;
import com.example.library.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        initializeUsers();
    }
    
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Initializing default users...");
            
            // Create Admin User
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles(Set.of("ADMIN"))
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: username=admin, password=admin123");
            
            // Create Librarian User
            User librarian = User.builder()
                    .username("librarian")
                    .password(passwordEncoder.encode("librarian123"))
                    .roles(Set.of("LIBRARIAN"))
                    .enabled(true)
                    .build();
            userRepository.save(librarian);
            log.info("Librarian user created: username=librarian, password=librarian123");
        }
    }
    // Removed duplicate inner DataInitializer class (was causing duplicate class error)
}
