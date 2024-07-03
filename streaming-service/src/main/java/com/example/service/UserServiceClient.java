package com.example.service;

import com.example.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    @GetMapping("/users/me")
    UserResponse getUserDetails(@RequestHeader("Authorization") String token);

    default UserResponse getUserDetailsWithLogging(String token) {
        UserResponse userResponse = getUserDetails(token);
        logger.debug("User details from UserServiceClient: " + userResponse);
        return userResponse;
    }
}
