package com.example.urlshortener.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/admin")
public class AdminAuthController {
    private final AdminCredentials adminCredentials;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Autowired
    public AdminAuthController(AdminCredentials adminCredentials, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.adminCredentials = adminCredentials;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public record LoginRequest(String username, String password) {
    }

    public record LoginResponse(String token, long expiresAt) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (!adminCredentials.getUsername().equals(req.username())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }
        if (!req.password().equals(adminCredentials.getPasswordHash())) {
            // ToDo: change above condition to passwordEncoder.matches
            log.info("Request password: {}, Password Hash: {}", req.password(), adminCredentials.getPasswordHash());
            return ResponseEntity.status(401).body(Map.of("error", "invalid credentials"));
        }

        String token = jwtUtils.generateAdminToken(req.username());
        long expiresAt = System.currentTimeMillis() + Long.parseLong(System.getProperty("jwt.exp-ms", "3600000"));
        return ResponseEntity.ok(new LoginResponse(token, expiresAt));
    }
}
