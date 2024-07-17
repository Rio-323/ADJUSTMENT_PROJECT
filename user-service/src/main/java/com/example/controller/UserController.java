package com.example.controller;

import com.example.entity.Role;
import com.example.entity.UserEntity;
import com.example.dto.UserDto;
import com.example.service.UserService;
import com.example.vo.RequestUser;
import com.example.vo.ResponseUser;
import io.jsonwebtoken.Jwts;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @PostMapping("/admin")
    public ResponseEntity<ResponseUser> createAdmin(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(user, UserDto.class);
        userDto.setRole(Role.ADMIN);

        UserDto createdAdmin = userService.createUser(userDto);
        ResponseUser responseAdmin = mapper.map(createdAdmin, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseAdmin);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();
        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> result.add(new ModelMapper().map(v, ResponseUser.class)));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
        UserDto userDto = userService.getUserById(userId);
        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }

    @GetMapping("/users/me")
    public ResponseEntity<ResponseUser> getUserDetails(@RequestHeader("Authorization") String token) {
        logger.info("Received request to get user details with token: {}", token);
        String jwt = token.replace("Bearer ", "");
        String userId = Jwts.parser()
                .setSigningKey(env.getProperty("token.secret").getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();

        logger.info("Extracted user ID from token: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);
        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            userService.blacklistToken(jwtToken);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
