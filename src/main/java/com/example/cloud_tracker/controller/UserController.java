package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.dto.PasswordUpdateDTO;
import com.example.cloud_tracker.dto.UserDTO;
import com.example.cloud_tracker.dto.UserProfileDTO;
import com.example.cloud_tracker.model.JwtResponse;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    User user = userService.register(userDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  // Todo : Invalid endpoint, authenticated endpoints shouldn't be redirected to
  // gh login page
  @PostMapping("/signin")
  public ResponseEntity<JwtResponse> login(@RequestBody UserDTO userDTO) {
    JwtResponse jwtResponse = userService.login(userDTO);
    return ResponseEntity.status(HttpStatus.OK).body(jwtResponse);
  }

  @GetMapping("/me/profile-picture")
  public ResponseEntity<String> getCurrentUserProfilePicture() {
    String profilePicture = userService.getCurrentUserProfilePicture();
    return ResponseEntity.ok(profilePicture);
  }

  @GetMapping("/me/name")
  public ResponseEntity<String> getCurrentUserName() {
    String userName = userService.getCurrentUserName();
    return ResponseEntity.ok(userName);
  }

  @GetMapping("/me/email")
  public ResponseEntity<String> getCurrentUserEmail() {
    String email = userService.getCurrentUserEmail();
    return ResponseEntity.ok(email);
  }

  @PatchMapping("/me/profile")
  public ResponseEntity<User> editProfile(@RequestBody UserDTO userDTO){
    User user = userService.editProfile(userDTO);
    return ResponseEntity.ok(user);
  }

  @PatchMapping("/me/password")
  public ResponseEntity<User> editPassword(@RequestBody PasswordUpdateDTO passwordUpdateDTO){
    User user = userService.editPassword(passwordUpdateDTO);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/me/profile")
  public ResponseEntity<UserProfileDTO> getUserProfileInfo() {
    UserProfileDTO userProfileInfo = userService.getUserProfileInfo();
    return ResponseEntity.status(HttpStatus.OK).body(userProfileInfo);
  }
}
