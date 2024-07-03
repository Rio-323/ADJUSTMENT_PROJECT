package com.example.dto;

import lombok.Data;

@Data
public class PauseRequest {
    private Long videoId;
    private String userId;
    private int currentPosition;
}
