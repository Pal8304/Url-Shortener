package com.example.urlshortener.controller;

import com.example.urlshortener.entity.ShortenRequest;
import com.example.urlshortener.entity.ShortenResponse;
import com.example.urlshortener.service.UrlShortenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @Autowired
    public UrlShortenController(UrlShortenService urlShortenService){
        this.urlShortenService = urlShortenService;
    }

    @PostMapping("/")
    public ShortenResponse saveShortenedUrl(@RequestBody ShortenRequest shortenRequest){
        log.info("Original String {}", shortenRequest.getOriginalUrl());
        return urlShortenService.saveUrl(shortenRequest.getOriginalUrl());
    }
}
