package com.example.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdDto {
    private Long id;
    private String title;
    private String url;
    private int viewCount;
    private List<Long> videoIds;
}
