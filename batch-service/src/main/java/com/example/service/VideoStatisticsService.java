package com.example.service;

import com.example.entity.VideoStatistics;
import com.example.repository.VideoStatisticsRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class VideoStatisticsService {

    private final VideoStatisticsRepository repository;

    public VideoStatisticsService(VideoStatisticsRepository repository) {
        this.repository = repository;
    }

    public List<VideoStatistics> getDailyTop5() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return repository.findTop5ByTypeAndPeriodOrderByVideoRankAsc("view", today);
    }

    public List<VideoStatistics> getWeeklyTop5() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);
        String week = startOfWeek.toString() + " ~ " + endOfWeek.toString();
        return repository.findTop5ByTypeAndPeriodOrderByVideoRankAsc("view", week);
    }

    public List<VideoStatistics> getMonthlyTop5() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        String month = startOfMonth.toString() + " ~ " + endOfMonth.toString();
        return repository.findTop5ByTypeAndPeriodOrderByVideoRankAsc("view", month);
    }
}