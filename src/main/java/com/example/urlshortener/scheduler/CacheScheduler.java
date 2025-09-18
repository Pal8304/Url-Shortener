package com.example.urlshortener.scheduler;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.exception.CacheException;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.service.UrlCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CacheScheduler {

    private final UrlRepository urlRepository;
    private final UrlCacheService urlCacheService;

    @Value("${url.cache.tok-k}")
    private Integer topK;

    @Autowired
    public CacheScheduler(UrlRepository urlRepository, UrlCacheService urlCacheService) {
        this.urlRepository = urlRepository;
        this.urlCacheService = urlCacheService;
    }

    @Scheduled(fixedDelay = 60000)
    public void refreshCachedUrls() {
        try {
            List<Url> topKUrls = urlRepository.findAll(
                    PageRequest.of(0, topK, Sort.by(Sort.Direction.DESC, "clickCount"))
            ).getContent();

            urlCacheService.updateRedisCache(topKUrls);
            log.info("Finished cache refresh: topK={}", topK);
        } catch (Exception ex) {
            log.error("Cache refresh failed", ex);
            throw new CacheException("Failed to refresh cache");
        }
    }
}
