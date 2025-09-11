package com.example.urlshortener.service;

import com.example.urlshortener.entity.ShortenResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlShortenerException;
import com.example.urlshortener.redis.UrlCacheService;
import com.example.urlshortener.repository.UrlRepository;
import com.privacylogistics.FF3Cipher;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@Slf4j
public class UrlShortenService {
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;

    private final Integer MAX_URL_LENGTH = 7;
    private final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Autowired
    public UrlShortenService(UrlRepository urlRepository, UrlCacheService urlCacheService) {
        this.urlRepository = urlRepository;
        this.urlCacheService = urlCacheService;
    }

    public ShortenResponse saveUrl(String originalUrl) {

        if (originalUrl.isBlank()) {
            throw new InvalidUrlException("URL shouldn't be empty");
        }

        // Checks if a duplicate originalUrl is asked to shorten
        Optional<Url> urlToCheck = urlRepository.findByOriginalUrl(originalUrl);
        if (urlToCheck.isPresent()) {
            return new ShortenResponse(urlToCheck.get().getShortUrl(), originalUrl);
        }

        try {
            Url url = new Url(originalUrl);
            url = urlRepository.save(url);

            Long urlId = url.getId();
            String shortenedUrl = generateShortenUrl(urlId);

            log.info("Shortened Url {} for Original Url {}", shortenedUrl, originalUrl);

            String encodedShortUrl = mystoEncoding(shortenedUrl);

            log.info("Encoded Url {} for Original Url {}", encodedShortUrl, originalUrl);

            url.setShortUrl(encodedShortUrl);
            urlCacheService.cacheUrl(url);
            urlRepository.save(url);

            return new ShortenResponse(encodedShortUrl, originalUrl);
        } catch (ConstraintViolationException e) {
            throw new InvalidUrlException("Provided Url is invalid");
        }
    }

    public String fetchOriginalUrl(String shortenUrl) throws UrlShortenerException {

        String cachedOriginalUrl = urlCacheService.getCachedUrl(shortenUrl);
        if (StringUtils.hasLength(cachedOriginalUrl)) {
            return cachedOriginalUrl;
        }

        Optional<Url> optionalUrl = urlRepository.findByShortUrl(shortenUrl);
        if (optionalUrl.isPresent() && !optionalUrl.get().getOriginalUrl().isBlank()) {
            Url url = optionalUrl.get();
            incrementUrlCounter(url);
            urlRepository.save(url);

            return url.getOriginalUrl();
        }
        throw new UrlShortenerException("No valid URL not found, please verify the link.");
    }

    private void incrementUrlCounter(Url url) {
        url.setClickCount(url.getClickCount() + 1);
    }

    private String generateShortenUrl(Long urlId) {
        return convertToBase62(urlId);
    }

    private String convertToBase62(Long urlId) {
        StringBuilder base62 = new StringBuilder();
        Long value = urlId;
        while (value > 0) {
            base62.append(BASE62_CHARS.charAt((int) (value % 62)));
            value /= 62;
        }

        while (base62.length() < MAX_URL_LENGTH) {
            base62.append(BASE62_CHARS.charAt(0));
        }

        log.info("For id {}, base62 is {}", urlId, base62);

        return base62.reverse().toString();
    }

    private String mystoEncoding(String shortUrl) {
        try {
            SecureRandom rnd = new SecureRandom();
            byte[] key = new byte[16];
            rnd.nextBytes(key);
            byte[] tweak = new byte[7];
            rnd.nextBytes(tweak);
            FF3Cipher ff3Cipher = new FF3Cipher(key, tweak, BASE62_CHARS);

            return ff3Cipher.encrypt(shortUrl, tweak);
        } catch (Exception e) {
            throw new UrlShortenerException("Error while encoding url", e);
        }
    }
}
