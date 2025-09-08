package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlShortenService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlShortenService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    /*
    ToDo: 1. Do we need to send url in request body or request params is fine ( so basically do I need to create a separate POJO )
          2. Figure out we'll redirect when a session is active
          3. How are we generating shortened URL, current idea: id in base 65 + salting
     */

    public String saveUrl(String originalUrl) {
        String shortUrl = shortenOriginalUrl(originalUrl);
        Url savedUrl = urlRepository.save(new Url(originalUrl, shortUrl));
        return savedUrl.getShortUrl();
    }

    private String shortenOriginalUrl(String originalUrl) {
        return originalUrl;
    }
}
