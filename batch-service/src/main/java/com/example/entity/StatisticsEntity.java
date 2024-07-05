package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "video_statistics")
public class StatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String period;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String videoTitle;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
