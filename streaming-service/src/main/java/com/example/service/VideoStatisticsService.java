package com.example.service;

import com.example.dto.VideoDto;
import com.example.dto.VideoStatistics;
import com.example.entity.VideoEntity;
import com.example.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VideoStatisticsService {

    private final VideoRepository videoRepository;

    public VideoStatistics getTop5VideosByViews(String period) {
        LocalDateTime startDate = getStartDate(period);
        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지에서 5개의 결과를 가져옵니다.
        List<VideoDto> top5Videos = videoRepository.findTopByViews(startDate, pageable).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return new VideoStatistics(top5Videos);
    }

    public VideoStatistics getTop5VideosByDuration(String period) {
        LocalDateTime startDate = getStartDate(period);
        Pageable pageable = PageRequest.of(0, 5); // 첫 페이지에서 5개의 결과를 가져옵니다.
        List<VideoEntity> videos = videoRepository.findTopByDuration(startDate, pageable);

        // 각 비디오의 실제 재생 시간을 계산
        Map<VideoEntity, Integer> videoPlayTimes = new HashMap<>();
        for (VideoEntity video : videos) {
            int totalPlayTime = video.getUserWatchPositions().values().stream().mapToInt(Integer::intValue).sum();
            videoPlayTimes.put(video, totalPlayTime);
        }

        // 재생 시간이 긴 순서대로 정렬하고 상위 5개를 선택
        List<VideoDto> top5Videos = videoPlayTimes.entrySet().stream()
                .sorted(Map.Entry.<VideoEntity, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> convertToDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new VideoStatistics(top5Videos);
    }

    private LocalDateTime getStartDate(String period) {
        LocalDate now = LocalDate.now();
        switch (period.toLowerCase()) {
            case "daily":
                return now.atStartOfDay();
            case "weekly":
                return now.with(DayOfWeek.MONDAY).atStartOfDay();
            case "monthly":
                return now.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            default:
                throw new IllegalArgumentException("Invalid period: " + period);
        }
    }

    private VideoDto convertToDto(VideoEntity videoEntity) {
        return new VideoDto(videoEntity.getTitle(), videoEntity.getViewCount(), videoEntity.getDuration());
    }

    private VideoDto convertToDto(VideoEntity videoEntity, int actualPlayTime) {
        return new VideoDto(videoEntity.getTitle(), videoEntity.getViewCount(), actualPlayTime);
    }
}