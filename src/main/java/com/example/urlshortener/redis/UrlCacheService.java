package com.example.urlshortener.redis;

import com.example.urlshortener.exception.CacheException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UrlCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String MAPPER_CACHE_PREFIX = "url:";
    private final String MAX_CLICK_COUNT_KEY = "max_click_count";

    private final Long MAX_CACHED_URLS = 100L;

    /**
     * We have 2 caches one for top X most clicked urls and other for shortUrl to { OriginalUrl, clickCount}
     */

    @Autowired
    public UrlCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheUrl(String shortUrl, String originalUrl, Long clickCount) throws CacheException {
        try {
            String cacheKey = MAPPER_CACHE_PREFIX + shortUrl;
            redisTemplate.opsForValue().set(cacheKey, new CachedUrl(originalUrl, clickCount));
            redisTemplate.opsForZSet().add(shortUrl, originalUrl, clickCount);
        } catch (Exception e) {
            throw new CacheException("Error while adding to mapper cache");
        }
    }

    public void evictUrl(String shortUrl) {
        try {
            String cacheKey = MAPPER_CACHE_PREFIX + shortUrl;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            throw new CacheException("Error occurred while evicting from cache");
        }
    }

    private String getCachedOriginalUrl(String shortUrl) {
        String cacheKey = MAPPER_CACHE_PREFIX + shortUrl;
        Object cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl instanceof CachedUrl) {
            return ((CachedUrl) cachedUrl).getOriginalUrl();
        }
        return null;
    }

}
