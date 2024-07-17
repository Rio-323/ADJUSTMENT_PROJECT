package com.example.repository;

import com.example.entity.VideoStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoStatisticsRepository extends JpaRepository<VideoStatistics, Long> {
    List<VideoStatistics> findTop5ByTypeAndPeriodOrderByVideoRankAsc(String type, String period);
}