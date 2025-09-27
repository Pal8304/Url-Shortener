package com.example.urlshortener.controller;

import com.example.urlshortener.dto.*;
import com.example.urlshortener.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginRequest loginRequest) {
        try {
            AdminLoginResponse response = adminService.authenticate(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("Error during admin login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AdminLoginResponse("Internal server error", null, false));
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard(@RequestHeader("Authorization") String token) {
        try {
            if (!adminService.isValidToken(extractTokenFromHeader(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            AdminDashboardResponse dashboard = adminService.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error getting admin dashboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/urls")
    public ResponseEntity<List<AdminUrlResponse>> getAllUrls(@RequestHeader("Authorization") String token) {
        try {
            if (!adminService.isValidToken(extractTokenFromHeader(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            List<AdminUrlResponse> urls = adminService.getAllUrls();
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            log.error("Error getting all URLs: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/urls/{id}")
    public ResponseEntity<AdminUrlResponse> getUrl(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            if (!adminService.isValidToken(extractTokenFromHeader(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            AdminUrlResponse url = adminService.getUrlById(id);
            return ResponseEntity.ok(url);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error getting URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/urls/{id}")
    public ResponseEntity<String> deleteUrl(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            if (!adminService.isValidToken(extractTokenFromHeader(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            adminService.deleteUrl(id);
            return ResponseEntity.ok("URL deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
        } catch (Exception e) {
            log.error("Error deleting URL: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<AdminDashboardResponse> getStats(@RequestHeader("Authorization") String token) {
        try {
            if (!adminService.isValidToken(extractTokenFromHeader(token))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            AdminDashboardResponse stats = adminService.getDashboardData();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting stats: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}
