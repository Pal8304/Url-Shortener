package com.example.urlshortener.config;

import com.example.urlshortener.entity.Admin;
import com.example.urlshortener.repository.AdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Override
    public void run(String... args) throws Exception {
        initializeDefaultAdmin();
    }
    
    private void initializeDefaultAdmin() {
        // Check if admin already exists
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setEmail("admin@urlshortener.com");
            admin.setIsActive(true);
            
            adminRepository.save(admin);
            log.info("Default admin user created - Username: admin, Password: admin123");
        } else {
            log.info("Admin user already exists");
        }
    }
}
