package com.example.dto;

import lombok.Data;

@Data
public class CreateAdRequest {
    private Long videoId;
    private String title;
    private String url;
}
