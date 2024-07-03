package com.example.dto;

import lombok.Data;

@Data
public class UserResponse {
    private String userId;
    private String email;
    private String name;
    private String role;
}
