package com.example.repository;

import com.example.entity.VideoRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRevenueRepository extends JpaRepository<VideoRevenue, Long> {
    List<VideoRevenue> findByPeriod(String period);
}