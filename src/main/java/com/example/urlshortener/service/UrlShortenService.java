package com.example.urlshortener.service;

import com.example.urlshortener.dto.ShortenResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.InvalidUrlException;
import com.example.urlshortener.exception.UrlShortenerException;
import com.example.urlshortener.repository.UrlRepository;
import com.privacylogistics.FF3Cipher;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.util.Optional;

@Service
@Slf4j
public class UrlShortenService {
    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;
    private final ClickCounterService clickCounterService;
    private final FF3Cipher ff3Cipher;

    private final Integer MAX_URL_LENGTH = 7;
    private final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Autowired
    public UrlShortenService(UrlRepository urlRepository, UrlCacheService urlCacheService, ClickCounterService clickCounterService, FF3Cipher ff3Cipher) {
        this.urlRepository = urlRepository;
        this.urlCacheService = urlCacheService;
        this.clickCounterService = clickCounterService;
        this.ff3Cipher = ff3Cipher;
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

            String encryptedShortUrl = generateShortenUrl(urlId);

            log.info("Encrypted Url {} for Original Url {}", encryptedShortUrl, originalUrl);

            url.setShortUrl(encryptedShortUrl);
            urlRepository.save(url);

            return new ShortenResponse(encryptedShortUrl, originalUrl);
        } catch (ConstraintViolationException e) {
            throw new InvalidUrlException("Provided Url is invalid");
        } catch (Exception e) {
            log.error("Failed to create short url for {}, reason: {}", originalUrl, e.getMessage());
            throw new UrlShortenerException("Internal error while encoding url", e);
        }
    }

    public String fetchOriginalUrl(String shortenUrl) throws UrlShortenerException {

        String cachedOriginalUrl = urlCacheService.getCachedUrl(shortenUrl);
        if (StringUtils.hasLength(cachedOriginalUrl)) {
            clickCounterService.incrementClickCountAsync(shortenUrl);
            return cachedOriginalUrl;
        }

        Optional<Url> optionalUrl = urlRepository.findByShortUrl(shortenUrl);
        if (optionalUrl.isPresent() && !optionalUrl.get().getOriginalUrl().isBlank()) {
            Url url = optionalUrl.get();
            clickCounterService.incrementClickCountAsync(url.getShortUrl());
            return url.getOriginalUrl();
        }
        throw new UrlShortenerException("No valid URL not found, please verify the link.");
    }

    private String generateShortenUrl(Long urlId) {
        try {
            String base62Url = convertToBase62(urlId);
            return ff3Cipher.encrypt(base62Url);
        } catch (BadPaddingException e) {
            throw new UrlShortenerException("Bad Padding Exception occurred", e);
        } catch (IllegalBlockSizeException e) {
            throw new UrlShortenerException("Illegal Block size exception occurred", e);
        } catch (Exception e) {
            throw new UrlShortenerException("Exception occurred while generating short url", e);
        }
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
}
