package com.example.repository;

import com.example.entity.VideoDailyViewCount;
import com.example.entity.VideoEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VideoDailyViewCountRepository extends CrudRepository<VideoDailyViewCount, Long> {
    // 특정 비디오의 지정된 날짜 범위의 일별 시청 수 목록을 조회
    List<VideoDailyViewCount> findByVideoAndDateBetween(VideoEntity video, LocalDate startDate, LocalDate endDate);

    // 특정 비디오의 지정된 날짜의 일별 시청 수를 조회
    VideoDailyViewCount findByVideoAndDate(VideoEntity video, LocalDate date);

    // 특정 비디오의 모든 일별 시청 수를 조회
    List<VideoDailyViewCount> findByVideo(VideoEntity video);

    // 특정 날짜 범위의 일별 시청 수 목록을 조회
    List<VideoDailyViewCount> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // 특정 날짜의 일별 시청 수를 조회
    List<VideoDailyViewCount> findByDate(LocalDate date);

    @Query("SELECT MIN(v.date) FROM VideoDailyViewCount v")
    Optional<LocalDate> findOldestDate();
}
