package com.example.client;

import com.example.dto.UserResponse;
import feign.FeignException;
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
        logger.debug("Token being sent: " + token);
        try {
            UserResponse userResponse = getUserDetails(token);
            logger.debug("User details from UserServiceClient: " + userResponse);
            return userResponse;
        } catch (FeignException e) {
            logger.error("Error occurred while fetching user details: " + e.getMessage());
            if (e.responseBody().isPresent()) {
                logger.error("Response body: " + e.responseBody().get());
            }
            throw e;
        }
    }
}