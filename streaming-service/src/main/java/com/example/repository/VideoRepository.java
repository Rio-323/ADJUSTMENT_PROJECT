package com.example.repository;

import com.example.entity.VideoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VideoRepository extends CrudRepository<VideoEntity, Long> {

    @Query("SELECT v FROM VideoEntity v WHERE v.createdAt >= :startDate ORDER BY v.viewCount DESC")
    List<VideoEntity> findTopByViews(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT v FROM VideoEntity v WHERE v.createdAt >= :startDate ORDER BY v.duration DESC")
    List<VideoEntity> findTopByDuration(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    List<VideoEntity> findAllByCreatedAtAfter(LocalDateTime startDate);
}
