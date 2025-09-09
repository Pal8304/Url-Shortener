package com.example.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shortened_url", unique = true)
    private String shortUrl;

    @URL(message = "Please provide a valid URL")
    @Column(name = "original_url", unique = true, nullable = false)
    private String originalUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Url(String originalUrl){
        this.originalUrl = originalUrl;
        this.createdAt = LocalDateTime.now();
        this.clickCount = 0L;
    }
}
