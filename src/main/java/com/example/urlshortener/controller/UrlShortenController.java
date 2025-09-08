package com.example.urlshortener.controller;

import com.example.urlshortener.entity.ShortenRequest;
import com.example.urlshortener.entity.ShortenResponse;
import com.example.urlshortener.service.UrlShortenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/api")
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @Autowired
    public UrlShortenController(UrlShortenService urlShortenService) {
        this.urlShortenService = urlShortenService;
    }

    @GetMapping("/{shortenUrl}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortenUrl) {
        String originalUrl = urlShortenService.fetchOriginalString(shortenUrl);
        log.info("Original Url {} from Shorten Url {}", originalUrl, shortenUrl);

        if (originalUrl != null) {
            return new RedirectView(originalUrl);
        }

        return new RedirectView("/404");
    }

    @PostMapping("/")
    public ShortenResponse saveShortenedUrl(@RequestBody ShortenRequest shortenRequest) {
        log.info("Original String {}", shortenRequest.getOriginalUrl());
        return urlShortenService.saveUrl(shortenRequest.getOriginalUrl());
    }
}
