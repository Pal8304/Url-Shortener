package com.example.urlshortener.exception;

public class UrlShortenerException extends RuntimeException {
    public UrlShortenerException(String message) {
        super(message);
    }

    public UrlShortenerException(String message, Exception e) {
        super(message, e);
    }
}
