package com.example.urlshortener.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.example.urlshortener.entity.Url;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUrlResponse {
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private String createdAt;
    private Long clickCount;

    public static AdminUrlResponse fromUrl(Url url) {
        return new AdminUrlResponse(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortUrl(),
                url.getCreatedAt() != null ? url.getCreatedAt().toString() : null,
                url.getClickCount()
        );
    }
}
