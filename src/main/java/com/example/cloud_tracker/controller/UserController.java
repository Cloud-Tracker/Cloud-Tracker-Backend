package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.dto.UserDTO;
import com.example.cloud_tracker.model.JwtResponse;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
            this.userService = userService;
        }
    @PostMapping("/test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.status(HttpStatus.OK).body("Test");
    }
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody @Valid UserDTO userDTO) {
        try {
            User user = userService.register(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // Todo : Invalid endpoint, authenticated endpoints shouldn't be redirected to gh login page
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> login(@RequestBody UserDTO userDTO) {
        try {
            JwtResponse jwtResponse = userService.login(userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
