package com.example.urlshortener.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginResponse {
    private String message;
    private String token;
    private boolean success;
}
