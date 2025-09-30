package com.example.urlshortener.service;

import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ClickCounterService {
    private final UrlRepository urlRepository;

    @Autowired
    public ClickCounterService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Async("clickCounterExecutor")
    @Transactional
    public void incrementClickCountAsync(String shortUrl) {
        try {
            int rows = urlRepository.incrementClickCountByShortUrl(shortUrl);
            if (rows == 0) {
                throw new InvalidUrlException("Provided short url is not valid");
            }
        } catch (Exception e) {
            log.error("Failed to increment click count for {}: {}", shortUrl, e.getMessage());
        }
    }
}
