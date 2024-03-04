package com.example.cloud_tracker.controller;
import com.example.cloud_tracker.controller.Oauth2Controller;

import com.example.cloud_tracker.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RestController
public class Home {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

}
