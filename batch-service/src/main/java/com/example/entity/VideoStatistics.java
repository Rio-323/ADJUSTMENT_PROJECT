package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "video_statistics")
public class VideoStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private String period;

    @Column(name = "video_rank", nullable = false)
    private int videoRank; // 변경된 부분

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int playTime;

    @Column(nullable = false)
    private String type; // "view" or "playTime"
}