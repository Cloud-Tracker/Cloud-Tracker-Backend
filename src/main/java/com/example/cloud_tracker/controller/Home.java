package com.example.cloud_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
  @GetMapping("/hello")
  public String hello() {
    return "hello";
  }
}
