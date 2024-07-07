package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "video_daily_view_counts")
public class VideoDailyViewCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private VideoEntity video;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int viewCount;
}
