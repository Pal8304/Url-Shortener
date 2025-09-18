package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UrlCacheService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${url.cache.prefix}")
    private String CACHE_PREFIX;

    @Autowired
    public UrlCacheService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String getCachedUrl(String shortUrl) {
        String cacheKey = CACHE_PREFIX + shortUrl;
        String originalCachedUrl = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasLength(originalCachedUrl)) {
            log.info("Cache hit for {}", shortUrl);
            return originalCachedUrl;
        }
        log.info("Cache miss for {}", shortUrl);
        return null;
    }

    public void updateRedisCache(List<Url> topKUrls) {
        if (topKUrls == null) return;

        Set<String> keysInCache = stringRedisTemplate.keys(CACHE_PREFIX + "*");
        Set<String> keysToDelete = new HashSet<>();
        Set<String> currentKeys = topKUrls.stream().map(url -> CACHE_PREFIX + url.getShortUrl()).collect(Collectors.toSet());

        for (String cacheKey : keysInCache) {
            if (!currentKeys.contains(cacheKey)) {
                keysToDelete.add(cacheKey);
            }
        }

        stringRedisTemplate.delete(keysToDelete);

        for (Url url : topKUrls) {
            if (!keysInCache.contains(CACHE_PREFIX + url.getShortUrl())) {
                stringRedisTemplate.opsForValue().set(CACHE_PREFIX + url.getShortUrl(), url.getOriginalUrl());
            }
        }
    }
}
