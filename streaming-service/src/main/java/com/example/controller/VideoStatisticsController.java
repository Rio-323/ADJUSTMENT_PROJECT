package com.example.controller;

import com.example.dto.VideoStatistics;
import com.example.service.VideoStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VideoStatisticsController {

    private final VideoStatisticsService videoStatisticsService;

    @GetMapping("/api/v1/videos/top5/views")
    public VideoStatistics getTop5VideosByViews(@RequestParam String period) {
        return videoStatisticsService.getTop5VideosByViews(period);
    }

    @GetMapping("/api/v1/videos/top5/duration")
    public VideoStatistics getTop5VideosByDuration(@RequestParam String period) {
        return videoStatisticsService.getTop5VideosByDuration(period);
    }
}
