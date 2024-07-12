package com.example.controller;

import com.example.dto.*;
import com.example.entity.AdEntity;
import com.example.entity.VideoEntity;
import com.example.service.StreamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class StreamingController {

    private final StreamingService streamingService;
    private static final Logger logger = LoggerFactory.getLogger(StreamingController.class);

    @Autowired
    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    // 비디오 재생 요청을 처리
    @PostMapping("/play")
    public ResponseEntity<Void> playVideo(@RequestBody PlayRequest playRequest) {
        logger.debug("Received play video request: {}", playRequest);
        streamingService.playVideo(playRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 비디오 일시 정지 요청을 처리
    @PostMapping("/pause")
    public ResponseEntity<Void> pauseVideo(@RequestBody PauseRequest pauseRequest) {
        logger.debug("Received pause video request: {}", pauseRequest);
        streamingService.pauseVideo(pauseRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 광고 시청 요청을 처리
    @PostMapping("/ads")
    public ResponseEntity<Void> adWatched(@RequestBody AdWatchedRequest adWatchedRequest) {
        logger.debug("Received ad watched request for adId: {}", adWatchedRequest.getAdId());
        streamingService.adWatched(adWatchedRequest.getAdId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 비디오 생성 요청을 처리
    @PostMapping("/videos")
    public ResponseEntity<VideoEntity> createVideo(@RequestBody CreateVideoRequest createVideoRequest) {
        VideoEntity video = streamingService.createVideo(createVideoRequest);
        return ResponseEntity.ok(video);
    }

    // 광고 생성 요청을 처리
    @PostMapping("/ads/create")
    public ResponseEntity<AdEntity> createAd(@RequestBody CreateAdRequest createAdRequest,
                                             @RequestHeader("Authorization") String token) {
        AdEntity ad = streamingService.createAd(createAdRequest, token);
        return ResponseEntity.ok(ad);
    }

    // 모든 광고를 조회하는 요청을 처리
    @GetMapping("/ads")
    public ResponseEntity<List<AdDto>> getAllAds() {
        List<AdDto> ads = streamingService.getAllAds();
        return ResponseEntity.ok(ads);
    }

    // 모든 비디오를 조회하는 요청을 처리
    @GetMapping("/videos")
    public ResponseEntity<List<VideoDto>> getAllVideos() {
        List<VideoDto> videos = streamingService.getAllVideos();
        return ResponseEntity.ok(videos);
    }

    // 특정 비디오와 광고의 전체 시청 수를 조회하는 요청을 처리
    @GetMapping("/counts")
    public ResponseEntity<Map<String, Integer>> getVideoAndAdCounts(@RequestParam Long videoId,
                                                                    @RequestParam Long adId) {
        logger.debug("Received request to get video and ad counts for videoId: {} and adId: {}", videoId, adId);
        Map<String, Integer> counts = streamingService.getVideoAndAdCounts(videoId, adId);
        return ResponseEntity.status(HttpStatus.OK).body(counts);
    }

    // 특정 비디오의 일별 시청 수를 조회하는 요청을 처리
    @GetMapping("/videos/{videoId}/daily-views")
    public ResponseEntity<List<VideoDailyViewCountDto>> getDailyVideoViewCount(@PathVariable Long videoId,
                                                                               @RequestParam String date) {
        logger.debug("Received request to get daily video view count for videoId: {} on date: {}", videoId, date);
        LocalDate localDate = LocalDate.parse(date);
        List<VideoDailyViewCountDto> dailyViewCounts = streamingService.getDailyVideoViewCount(videoId, localDate);
        return ResponseEntity.status(HttpStatus.OK).body(dailyViewCounts);
    }

    // 특정 광고의 일별 시청 수를 조회하는 요청을 처리
    @GetMapping("/ads/{adId}/daily-views")
    public ResponseEntity<List<AdDailyViewCountDto>> getDailyAdViewCount(@PathVariable Long adId,
                                                                         @RequestParam String date) {
        logger.debug("Received request to get daily ad view count for adId: {} on date: {}", adId, date);
        LocalDate localDate = LocalDate.parse(date);
        List<AdDailyViewCountDto> dailyViewCounts = streamingService.getDailyAdViewCount(adId, localDate);
        return ResponseEntity.status(HttpStatus.OK).body(dailyViewCounts);
    }

    @GetMapping("/streaming-service/videos/daily-views")
    public List<VideoDailyViewCountDto> getDailyVideoStatistics(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return streamingService.getDailyVideoStatistics(localDate);
    }

    @GetMapping("/streaming-service/videos/weekly-views")
    public List<VideoDailyViewCountDto> getWeeklyVideoStatistics(@RequestParam String startDate,
                                                                 @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return streamingService.getWeeklyVideoStatistics(start, end);
    }

    @GetMapping("/streaming-service/videos/monthly-views")
    public List<VideoDailyViewCountDto> getMonthlyVideoStatistics(@RequestParam String startDate,
                                                                  @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return streamingService.getMonthlyVideoStatistics(start, end);
    }
}