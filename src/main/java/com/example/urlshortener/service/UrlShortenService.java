package com.example.urlshortener.service;

import com.example.urlshortener.entity.ShortenResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UrlShortenService {
    private final UrlRepository urlRepository;

    @Autowired
    public UrlShortenService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public ShortenResponse saveUrl(String originalUrl) {
        Url url = new Url(originalUrl);
        url = urlRepository.save(url);

        Long urlId = url.getId();
        String shortenedUrl = generateShortenUrl(urlId);

        log.info("Shortened Url {} for Original Url {}", shortenedUrl, originalUrl);

        url.setShortUrl(shortenedUrl);
        urlRepository.save(url);

        return new ShortenResponse(shortenedUrl, originalUrl);
    }

    public String fetchOriginalString(String shortenUrl) {
        Optional<Url> optionalUrl = urlRepository.findByShortUrl(shortenUrl);

        if (optionalUrl.isPresent()) {
            Url url = optionalUrl.get();
            return url.getOriginalUrl();
        } else {
            return null;
        }
    }

    private String generateShortenUrl(Long urlId) {
        return convertToBase62(urlId);
    }

    private String convertToBase62(Long urlId) {
        final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        StringBuilder base62 = new StringBuilder();
        Long value = urlId;
        while (value > 0) {
            base62.append(BASE62_CHARS.charAt((int) (value % 62)));
            value /= 62;
        }

        while (base62.length() < 7) {
            base62.append(BASE62_CHARS.charAt(0));
        }

        log.info("For id {}, base62 is {}", urlId, base62);

        return base62.reverse().toString();
    }
}
