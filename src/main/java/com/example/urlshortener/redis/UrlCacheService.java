package com.example.urlshortener.redis;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.CacheException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UrlCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Long TIME_OUT = 60L;

    @Autowired
    public UrlCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheUrl(Url url) {
        try{
            redisTemplate.opsForValue().set(url.getShortUrl(), url.getOriginalUrl(), TIME_OUT, TimeUnit.SECONDS);
            log.info("Cached data for url: {}", url.getOriginalUrl());
        } catch (Exception e){
            throw new CacheException("Error while caching url: " + url.getOriginalUrl(), e);
        }
    }

    public String getCachedUrl(String shortUrl){
        String originalCachedUrl = redisTemplate.opsForValue().get(shortUrl);
        if(!StringUtils.hasLength(originalCachedUrl)) {
            log.info("Cache hit for {}", shortUrl);
            return originalCachedUrl;
        }
        log.info("Cache miss for {}", shortUrl);
        return null;
    }
}
