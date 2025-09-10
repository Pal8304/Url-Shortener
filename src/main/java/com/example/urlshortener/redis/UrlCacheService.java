package com.example.urlshortener.redis;

import com.example.urlshortener.entity.Url;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UrlCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UrlCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final String MAPPER_CACHE_PREFIX = "url:";
    private final String MAX_CLICK_COUNT_KEY = "max_click_count";

    private final Long MAX_CACHED_URLS = 100L;

    /**
     * We have 2 caches one for top X most clicked urls and other for shortUrl to { OriginalUrl, clickCount}
     */

    private void cacheUrl(String shortUrl, String originalUrl, Long clickCount) {
        String cacheKey = MAPPER_CACHE_PREFIX + shortUrl;

        Long cachedUrlCount = redisTemplate.opsForZSet().count(MAX_CLICK_COUNT_KEY, Double.MIN_VALUE, Double.MAX_VALUE);
        if(cachedUrlCount != null && cachedUrlCount < MAX_CACHED_URLS) {
            redisTemplate.opsForZSet().add(MAX_CLICK_COUNT_KEY, shortUrl, clickCount);
            redisTemplate.opsForValue().set(cacheKey, new CachedUrl(originalUrl, clickCount));
        }
    }

    private void cacheUrl(Url urlToCache) {
        cacheUrl(urlToCache.getShortUrl(), urlToCache.getOriginalUrl(), urlToCache.getClickCount());
    }

    private String getCachedOriginalUrl(String shortUrl) {
        String cacheKey = MAPPER_CACHE_PREFIX + shortUrl;
        Object cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(cacheValue instanceof CachedUrl) {
            return ((CachedUrl) cacheValue).getOriginalUrl();
        }
        return null;
    }
}
