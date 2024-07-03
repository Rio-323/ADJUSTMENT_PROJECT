package com.example.service;

import com.example.dto.*;
import com.example.entity.*;
import com.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreamingService {

    private final VideoRepository videoRepository;
    private final AdRepository adRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public StreamingService(VideoRepository videoRepository, AdRepository adRepository, UserServiceClient userServiceClient) {
        this.videoRepository = videoRepository;
        this.adRepository = adRepository;
        this.userServiceClient = userServiceClient;
    }

    public void playVideo(PlayRequest playRequest) {
        VideoEntity video = videoRepository.findById(playRequest.getVideoId()).orElseThrow();
        int currentPosition = video.getUserWatchPositions().getOrDefault(playRequest.getUserId(), 0);
        video.setViewCount(video.getViewCount() + 1);
        videoRepository.save(video);
        System.out.println("비디오 재생: " + video.getTitle() + ", 현재 위치: " + currentPosition);
    }

    public void pauseVideo(PauseRequest pauseRequest) {
        VideoEntity video = videoRepository.findById(pauseRequest.getVideoId()).orElseThrow();
        video.getUserWatchPositions().put(pauseRequest.getUserId(), pauseRequest.getCurrentPosition());
        videoRepository.save(video);
        System.out.println("비디오 중단: " + video.getTitle() + ", 중단 위치: " + pauseRequest.getCurrentPosition());
    }

    public void adWatched(Long adId) {
        AdEntity ad = adRepository.findById(adId).orElseThrow();
        ad.setViewCount(ad.getViewCount() + 1);
        adRepository.save(ad);

        VideoEntity video = ad.getVideo();
        videoRepository.save(video);

        System.out.println("광고 시청: " + ad.getTitle() + ", 시청 횟수: " + ad.getViewCount());
    }

    public void createAd(CreateAdRequest createAdRequest, String token) {
        UserResponse user = userServiceClient.getUserDetails(token);
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Only admins can create ads.");
        }

        VideoEntity video = videoRepository.findById(createAdRequest.getVideoId()).orElseThrow();
        AdEntity ad = new AdEntity();
        ad.setTitle(createAdRequest.getTitle());
        ad.setUrl(createAdRequest.getUrl());
        ad.setVideo(video);
        adRepository.save(ad);
        System.out.println("광고 생성: " + ad.getTitle() + ", 비디오: " + video.getTitle());
    }

    public VideoEntity createVideo(CreateVideoRequest createVideoRequest) {
        VideoEntity video = new VideoEntity();
        video.setTitle(createVideoRequest.getTitle());
        video.setUrl(createVideoRequest.getUrl());
        video.setDuration(createVideoRequest.getDuration());
        return videoRepository.save(video);
    }

    public List<AdEntity> getAllAds() {
        return (List<AdEntity>) adRepository.findAll();
    }

    public List<VideoEntity> getAllVideos() {
        return (List<VideoEntity>) videoRepository.findAll();
    }

    public Map<String, Integer> getVideoAndAdCounts(Long videoId, Long adId) {
        VideoEntity video = videoRepository.findById(videoId).orElseThrow();
        AdEntity ad = adRepository.findById(adId).orElseThrow();

        if (!ad.getVideo().getId().equals(videoId)) {
            throw new RuntimeException("Ad does not belong to the specified video.");
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("videoViewCount", video.getViewCount());
        counts.put("adViewCount", ad.getViewCount());

        return counts;
    }
}
