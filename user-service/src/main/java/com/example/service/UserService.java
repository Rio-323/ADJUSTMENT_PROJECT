package com.example.service;

import com.example.entity.UserEntity;
import com.example.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(String userId);
    Iterable<UserEntity> getUserByAll();
    UserDto getUserDetailsByEmail(String email);
    void blacklistToken(String token);
    boolean isTokenBlacklisted(String token);
    String createJwtToken(UserDto userDetails); // JWT 토큰 생성 메서드
}
