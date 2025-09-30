package com.example.urlshortener.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardResponse {
    private Long totalUrls;
    private Long totalClicks;
    private Long averageClicksPerUrl;
    private List<AdminUrlResponse> topUrls;
    private List<AdminUrlResponse> recentUrls;
}
