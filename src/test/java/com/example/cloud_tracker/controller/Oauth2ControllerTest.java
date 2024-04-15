package com.example.cloud_tracker.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Oauth2ControllerTest {

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private Oauth2Controller oauth2Controller;

    @Test
    public void testUser() {
        MockitoAnnotations.openMocks(this);
        when(oAuth2User.getAttribute("name")).thenReturn("John Doe");

        Map<String, Object> response = oauth2Controller.user(oAuth2User);

        assertEquals(Collections.singletonMap("name", "John Doe"), response);
    }
}

