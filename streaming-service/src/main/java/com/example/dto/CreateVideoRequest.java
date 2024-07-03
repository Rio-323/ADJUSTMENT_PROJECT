package com.example.dto;

import lombok.Data;

@Data
public class CreateVideoRequest {
    private String title;
    private String url;
    private int duration;
}
