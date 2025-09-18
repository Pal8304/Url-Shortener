package com.example.urlshortener.config;

import com.privacylogistics.FF3Cipher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class FPEConfig {

    @Value("${url.base62-chars}")
    private String ALPHABET;

    @Value("${fpe.key-base64}")
    private String keyBase64;

    @Value("${fpe.tweak-base64}")
    private String tweakBase64;

    @Bean
    public FF3Cipher ff3Cipher(){
        byte[] key = Base64.getDecoder().decode(keyBase64);
        byte[] tweak = Base64.getDecoder().decode(tweakBase64);

        return new FF3Cipher(key, tweak, ALPHABET);
    }

}
