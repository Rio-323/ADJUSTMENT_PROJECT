package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDto {
    private String title;
    private int viewCount;
    private int durationOrPlayTime; // 실제 재생 시간 또는 비디오 길이
}
