package com.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "video_revenue")
public class VideoRevenue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private String period;

    @Column(nullable = false)
    private String totalRevenue;

    @Column(nullable = false)
    private String videoRevenue;

    @Column(nullable = false)
    private String adRevenue;
}