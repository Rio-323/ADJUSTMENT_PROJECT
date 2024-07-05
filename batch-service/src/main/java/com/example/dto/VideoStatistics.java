package com.example.dto;

import com.example.entity.VideoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoStatistics {
    private List<VideoEntity> topVideos;
}
