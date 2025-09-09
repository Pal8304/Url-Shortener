package com.example.urlshortener.exception;

public class InvalidUrlException extends RuntimeException {
    public InvalidUrlException() {
        super("Provided Url is invalid");
    }

    public InvalidUrlException(String message) {
        super(message);
    }
}
