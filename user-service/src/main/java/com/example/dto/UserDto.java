package com.example.dto;

import com.example.entity.Role;
import lombok.Data;

@Data
public class UserDto {
    private String email;
    private String name;
    private String password;
    private String userId;
    private String encryptedPassword;
    private Role role;
}
