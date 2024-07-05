package com.example.client;

import com.example.dto.VideoStatistics;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "streaming-service")
public interface StreamingServiceClient {

    @GetMapping("/api/v1/videos/top5/views")
    VideoStatistics getTop5VideosByViews(@RequestParam("period") String period);

    @GetMapping("/api/v1/videos/top5/duration")
    VideoStatistics getTop5VideosByDuration(@RequestParam("period") String period);
}
