package com.example.urlshortener.controller;

import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.service.UrlCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;

    @Autowired
    public AdminController(UrlRepository urlRepository, UrlCacheService urlCacheService) {
        this.urlRepository = urlRepository;
        this.urlCacheService = urlCacheService;
    }

    @GetMapping("/urls")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    @GetMapping("/cache")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ShortenResponse> getCacheContents() {
        return urlCacheService.getAllCachedEntries();
    }
}
