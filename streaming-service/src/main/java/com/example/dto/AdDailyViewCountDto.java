package com.example.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdDailyViewCountDto {
    private Long id;
    private Long adId;
    private LocalDate date;
    private int viewCount;
}
