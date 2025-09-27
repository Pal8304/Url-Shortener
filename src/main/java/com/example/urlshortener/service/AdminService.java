package com.example.urlshortener.service;

import com.example.urlshortener.dto.*;
import com.example.urlshortener.entity.Admin;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.AdminRepository;
import com.example.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UrlRepository urlRepository;
    
    // Simple token validation - in production, use proper JWT validation
    
    public AdminLoginResponse authenticate(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsernameAndIsActiveTrue(username);
        
        if (admin.isPresent() && admin.get().getPassword().equals(password)) {
            // Update last login
            Admin adminEntity = admin.get();
            adminEntity.setLastLogin(LocalDateTime.now());
            adminRepository.save(adminEntity);
            
            log.info("Admin {} authenticated successfully", username);
            return new AdminLoginResponse("Login successful", generateSimpleToken(username), true);
        }
        
        log.warn("Failed authentication attempt for username: {}", username);
        return new AdminLoginResponse("Invalid credentials", null, false);
    }
    
    public List<AdminUrlResponse> getAllUrls() {
        List<Url> urls = urlRepository.findAll();
        return urls.stream()
                .map(AdminUrlResponse::fromUrl)
                .collect(Collectors.toList());
    }
    
    public AdminDashboardResponse getDashboardData() {
        List<Url> allUrls = urlRepository.findAll();
        
        long totalUrls = allUrls.size();
        long totalClicks = allUrls.stream().mapToLong(Url::getClickCount).sum();
        long averageClicksPerUrl = totalUrls > 0 ? totalClicks / totalUrls : 0;
        
        // Get top 10 most clicked URLs
        List<AdminUrlResponse> topUrls = allUrls.stream()
                .sorted((u1, u2) -> Long.compare(u2.getClickCount(), u1.getClickCount()))
                .limit(10)
                .map(AdminUrlResponse::fromUrl)
                .collect(Collectors.toList());
        
        // Get recent 10 URLs
        List<AdminUrlResponse> recentUrls = allUrls.stream()
                .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
                .limit(10)
                .map(AdminUrlResponse::fromUrl)
                .collect(Collectors.toList());
        
        return new AdminDashboardResponse(totalUrls, totalClicks, averageClicksPerUrl, topUrls, recentUrls);
    }
    
    public AdminUrlResponse getUrlById(Long id) {
        Optional<Url> url = urlRepository.findById(id);
        return url.map(AdminUrlResponse::fromUrl)
                 .orElseThrow(() -> new RuntimeException("URL not found"));
    }
    
    public void deleteUrl(Long id) {
        if (urlRepository.existsById(id)) {
            urlRepository.deleteById(id);
            log.info("URL with ID {} deleted", id);
        } else {
            throw new RuntimeException("URL not found");
        }
    }
    
    private String generateSimpleToken(String username) {
        // Simple token generation - in production, use JWT or similar
        return "admin_token_" + username + "_" + System.currentTimeMillis();
    }
    
    public boolean isValidToken(String token) {
        // Simple token validation - in production, use proper JWT validation
        return token != null && token.startsWith("admin_token_");
    }
}
