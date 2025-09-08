package com.example.urlshortener.controller;

import com.example.urlshortener.service.UrlShortenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @Autowired
    public UrlShortenController(UrlShortenService urlShortenService){
        this.urlShortenService = urlShortenService;
    }

    @PostMapping("/")
    public String saveShortenedUrl(@RequestBody String originalUrl){
        log.info("Original String {}", originalUrl);
        return urlShortenService.saveUrl(originalUrl);
    }
}
