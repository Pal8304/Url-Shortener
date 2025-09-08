package com.example.urlshortener.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortenResponse {
    private String shortUrl;
    private String originalUrl;
}
