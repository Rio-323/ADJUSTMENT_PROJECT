package com.example.service;

import com.example.client.UserServiceClient;
import com.example.dto.*;
import com.example.entity.AdDailyViewCount;
import com.example.entity.AdEntity;
import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoEntity;
import com.example.repository.AdDailyViewCountRepository;
import com.example.repository.AdRepository;
import com.example.repository.VideoDailyViewCountRepository;
import com.example.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StreamingService {

    private final VideoRepository videoRepository;
    private final AdRepository adRepository;
    private final VideoDailyViewCountRepository videoDailyViewCountRepository;
    private final AdDailyViewCountRepository adDailyViewCountRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public StreamingService(VideoRepository videoRepository, AdRepository adRepository,
                            VideoDailyViewCountRepository videoDailyViewCountRepository,
                            AdDailyViewCountRepository adDailyViewCountRepository,
                            UserServiceClient userServiceClient) {
        this.videoRepository = videoRepository;
        this.adRepository = adRepository;
        this.videoDailyViewCountRepository = videoDailyViewCountRepository;
        this.adDailyViewCountRepository = adDailyViewCountRepository;
        this.userServiceClient = userServiceClient;
    }

    // 비디오 재생 시 호출되는 메서드
    public void playVideo(PlayRequest playRequest) {
        VideoEntity video = videoRepository.findById(playRequest.getVideoId()).orElseThrow();
        int currentPosition = video.getUserWatchPositions().getOrDefault(playRequest.getUserId(), 0);
        video.setViewCount(video.getViewCount() + 1);

        LocalDate today = LocalDate.now();
        VideoDailyViewCount dailyViewCount = videoDailyViewCountRepository.findByVideoAndDate(video, today);
        if (dailyViewCount == null) {
            dailyViewCount = new VideoDailyViewCount();
            dailyViewCount.setVideo(video);
            dailyViewCount.setDate(today);
            dailyViewCount.setViewCount(1);
        } else {
            dailyViewCount.setViewCount(dailyViewCount.getViewCount() + 1);
        }
        videoDailyViewCountRepository.save(dailyViewCount);

        videoRepository.save(video);
        validateViewCounts(video);
    }

    // 비디오 일시 정지 시 호출되는 메서드
    public void pauseVideo(PauseRequest pauseRequest) {
        VideoEntity video = videoRepository.findById(pauseRequest.getVideoId()).orElseThrow();
        int pausePosition = pauseRequest.getCurrentPosition();

        if (pausePosition <= video.getDuration()) {
            video.getUserWatchPositions().put(pauseRequest.getUserId(), pausePosition);
            videoRepository.save(video);
        }
    }

    // 광고 시청 시 호출되는 메서드
    public void adWatched(Long adId) {
        AdEntity ad = adRepository.findById(adId).orElseThrow();
        ad.setViewCount(ad.getViewCount() + 1);

        LocalDate today = LocalDate.now();
        AdDailyViewCount dailyViewCount = adDailyViewCountRepository.findByAdAndDate(ad, today);
        if (dailyViewCount == null) {
            dailyViewCount = new AdDailyViewCount();
            dailyViewCount.setAd(ad);
            dailyViewCount.setDate(today);
            dailyViewCount.setViewCount(1);
        } else {
            dailyViewCount.setViewCount(dailyViewCount.getViewCount() + 1);
        }
        adDailyViewCountRepository.save(dailyViewCount);

        adRepository.save(ad);
        validateAdViewCounts(ad);
    }

    // 광고 생성 시 호출되는 메서드
    public AdEntity createAd(CreateAdRequest createAdRequest, String token) {
        UserResponse user = userServiceClient.getUserDetails(token);
        if (!"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Only admins can create ads.");
        }

        VideoEntity video = videoRepository.findById(createAdRequest.getVideoId())
                .orElseThrow(() -> new RuntimeException("Video not found"));

        AdEntity ad = new AdEntity();
        ad.setTitle(createAdRequest.getTitle());
        ad.setUrl(createAdRequest.getUrl());
        ad.setViewCount(0);

        ad = adRepository.save(ad);

        video.getAds().add(ad);
        videoRepository.save(video);

        return ad;
    }

    // 비디오 생성 시 호출되는 메서드
    public VideoEntity createVideo(CreateVideoRequest createVideoRequest) {
        VideoEntity video = new VideoEntity();
        video.setTitle(createVideoRequest.getTitle());
        video.setUrl(createVideoRequest.getUrl());
        video.setDuration(createVideoRequest.getDuration());
        return videoRepository.save(video);
    }

    // 모든 광고를 조회하는 메서드
    public List<AdDto> getAllAds() {
        List<AdEntity> ads = (List<AdEntity>) adRepository.findAll();
        return ads.stream().map(this::convertToAdDto).collect(Collectors.toList());
    }
    // 모든 비디오를 조회하는 메서드
    public List<VideoDto> getAllVideos() {
        List<VideoEntity> videos = videoRepository.findAll();
        return videos.stream().map(this::convertToVideoDto).collect(Collectors.toList());
    }


    private VideoDto convertToVideoDto(VideoEntity video) {
        VideoDto videoDto = new VideoDto();
        videoDto.setId(video.getId());
        videoDto.setTitle(video.getTitle());
        videoDto.setUrl(video.getUrl());
        videoDto.setViewCount(video.getViewCount());
        videoDto.setDuration(video.getDuration());
        videoDto.setAds(video.getAds().stream().map(this::convertToAdDto).collect(Collectors.toList()));
        return videoDto;
    }

    private AdDto convertToAdDto(AdEntity ad) {
        AdDto adDto = new AdDto();
        adDto.setId(ad.getId());
        adDto.setTitle(ad.getTitle());
        adDto.setUrl(ad.getUrl());
        adDto.setViewCount(ad.getViewCount());
        adDto.setVideoIds(ad.getVideos().stream().map(VideoEntity::getId).collect(Collectors.toList()));
        return adDto;
    }

    // 특정 비디오와 광고의 전체 시청 수를 조회하는 메서드
    public Map<String, Integer> getVideoAndAdCounts(Long videoId, Long adId) {
        VideoEntity video = videoRepository.findById(videoId).orElseThrow();
        AdEntity ad = adRepository.findById(adId).orElseThrow();

        if (!ad.getVideos().contains(video)) {
            throw new RuntimeException("Ad does not belong to the specified video.");
        }

        Map<String, Integer> counts = new HashMap<>();
        counts.put("videoViewCount", video.getViewCount());
        counts.put("adViewCount", ad.getViewCount());

        return counts;
    }

    // 특정 비디오의 일별 시청 수를 조회하는 메서드
    public List<VideoDailyViewCountDto> getDailyVideoViewCount(Long videoId, LocalDate date) {
        VideoEntity video = videoRepository.findById(videoId).orElseThrow();
        List<VideoDailyViewCount> dailyViewCounts = videoDailyViewCountRepository.findByVideoAndDateBetween(video, date, date);
        return dailyViewCounts.stream().map(this::convertToVideoDailyViewCountDto).collect(Collectors.toList());
    }

    // 특정 광고의 일별 시청 수를 조회하는 메서드
    public List<AdDailyViewCountDto> getDailyAdViewCount(Long adId, LocalDate date) {
        AdEntity ad = adRepository.findById(adId).orElseThrow();
        List<AdDailyViewCount> dailyViewCounts = adDailyViewCountRepository.findByAdAndDateBetween(ad, date, date);
        return dailyViewCounts.stream().map(this::convertToAdDailyViewCountDto).collect(Collectors.toList());
    }

    private VideoDailyViewCountDto convertToVideoDailyViewCountDto(VideoDailyViewCount entity) {
        VideoDailyViewCountDto dto = new VideoDailyViewCountDto();
        dto.setId(entity.getId());
        dto.setVideoId(entity.getVideo().getId());
        dto.setDate(entity.getDate());
        dto.setViewCount(entity.getViewCount());
        return dto;
    }

    private AdDailyViewCountDto convertToAdDailyViewCountDto(AdDailyViewCount entity) {
        AdDailyViewCountDto dto = new AdDailyViewCountDto();
        dto.setId(entity.getId());
        dto.setAdId(entity.getAd().getId());
        dto.setDate(entity.getDate());
        dto.setViewCount(entity.getViewCount());
        return dto;
    }

    // 비디오의 총 시청 수가 일별 시청 수의 합과 일치하는지 검증
    private void validateViewCounts(VideoEntity video) {
        List<VideoDailyViewCount> dailyViewCounts = videoDailyViewCountRepository.findByVideo(video);
        int totalDailyViews = dailyViewCounts.stream().mapToInt(VideoDailyViewCount::getViewCount).sum();
        if (totalDailyViews != video.getViewCount()) {
            throw new IllegalStateException("비디오의 총 시청 수와 일별 시청 수의 합이 일치하지 않습니다.");
        }
    }

    // 광고의 총 시청 수가 일별 시청 수의 합과 일치하는지 검증
    private void validateAdViewCounts(AdEntity ad) {
        List<AdDailyViewCount> dailyViewCounts = adDailyViewCountRepository.findByAd(ad);
        int totalDailyViews = dailyViewCounts.stream().mapToInt(AdDailyViewCount::getViewCount).sum();
        if (totalDailyViews != ad.getViewCount()) {
            throw new IllegalStateException("광고의 총 시청 수와 일별 시청 수의 합이 일치하지 않습니다.");
        }
    }
}
