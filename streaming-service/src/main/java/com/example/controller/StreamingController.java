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

    @PostMapping("/play")
    public ResponseEntity<Void> playVideo(@RequestBody PlayRequest playRequest) {
        logger.debug("Received play video request: {}", playRequest);
        streamingService.playVideo(playRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/pause")
    public ResponseEntity<Void> pauseVideo(@RequestBody PauseRequest pauseRequest) {
        logger.debug("Received pause video request: {}", pauseRequest);
        streamingService.pauseVideo(pauseRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/ads")
    public ResponseEntity<Void> adWatched(@RequestBody AdWatchedRequest adWatchedRequest) {
        logger.debug("Received ad watched request for adId: {}", adWatchedRequest.getAdId());
        streamingService.adWatched(adWatchedRequest.getAdId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/videos")
    public ResponseEntity<VideoEntity> createVideo(@RequestBody CreateVideoRequest createVideoRequest) {
        logger.debug("Received request to create video: {}", createVideoRequest);
        VideoEntity createdVideo = streamingService.createVideo(createVideoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVideo);
    }

    @PostMapping("/ads/create")
    public ResponseEntity<Void> createAd(@RequestBody CreateAdRequest createAdRequest, @RequestHeader("Authorization") String token) {
        logger.debug("Received request to create ad: {}", createAdRequest);
        streamingService.createAd(createAdRequest, token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ads")
    public ResponseEntity<List<AdEntity>> getAllAds() {
        logger.debug("Received request to get all ads");
        List<AdEntity> ads = streamingService.getAllAds();
        return ResponseEntity.status(HttpStatus.OK).body(ads);
    }

    @GetMapping("/videos")
    public ResponseEntity<List<VideoEntity>> getAllVideos() {
        logger.debug("Received request to get all videos");
        List<VideoEntity> videos = streamingService.getAllVideos();
        return ResponseEntity.status(HttpStatus.OK).body(videos);
    }

    @GetMapping("/videos/{videoId}/ads/{adId}/counts")
    public Map<String, Integer> getVideoAndAdCounts(@PathVariable Long videoId, @PathVariable Long adId) {
        return streamingService.getVideoAndAdCounts(videoId, adId);
    }
}
