package com.example.urlshortener.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException(Exception e) {
        super("Provided Url is invalid", e);
    }

    public InvalidUrlException(String message) {
        super(message);
    }
}
