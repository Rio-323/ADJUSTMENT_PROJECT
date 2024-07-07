package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "ad_daily_view_counts")
public class AdDailyViewCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private AdEntity ad;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int viewCount;
}
