package com.example.cloud_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class Home {
    @GetMapping("/")
    public String home() {
        return "Hello, World!";
    }

    @PostMapping("/hello")
    public String postMethodName(@RequestParam String text) {
        return text;
    }

}
