package com.example.urlshortener.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CachedUrl {
    private String originalUrl;
    private Long clickCount;
}
