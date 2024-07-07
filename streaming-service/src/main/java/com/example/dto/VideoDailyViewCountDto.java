package com.example.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VideoDailyViewCountDto {
    private Long id;
    private Long videoId;
    private LocalDate date;
    private int viewCount;
}
