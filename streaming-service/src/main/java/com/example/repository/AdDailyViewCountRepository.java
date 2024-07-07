package com.example.repository;


import com.example.entity.AdDailyViewCount;
import com.example.entity.AdEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface AdDailyViewCountRepository extends CrudRepository<AdDailyViewCount, Long> {

    // 특정 광고의 지정된 날짜 범위의 일별 시청 수 목록을 조회합니다.
    List<AdDailyViewCount> findByAdAndDateBetween(AdEntity ad, LocalDate startDate, LocalDate endDate);

     // 특정 광고의 지정된 날짜의 일별 시청 수를 조회합니다.
    AdDailyViewCount findByAdAndDate(AdEntity ad, LocalDate date);

    // 특정 광고의 모든 일별 시청 수를 조회
    List<AdDailyViewCount> findByAd(AdEntity ad);
}
