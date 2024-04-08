package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.dto.BlogDTO;
import com.example.cloud_tracker.model.Blog;
import com.example.cloud_tracker.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

class BlogControllerTest {

    @Mock
    private BlogService blogService;

    @InjectMocks
    private BlogController blogController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveBlog() {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setHtmlContent("<p>Test content</p>");

        ResponseEntity<String> expectedResponse = ResponseEntity.ok("Blog saved successfully");

        when(blogService.saveBlog(anyString())).thenReturn(expectedResponse);

        ResponseEntity<String> response = blogController.saveBlog(blogDTO);

        assertEquals(expectedResponse, response);
        verify(blogService, times(1)).saveBlog(anyString());
    }

    @Test
    void getBlog() {
        ArrayList<String> expectedBlogs = new ArrayList<>();
        expectedBlogs.add("<p>Blog 1</p>");
        expectedBlogs.add("<p>Blog 2</p>");

        when(blogService.getBlogs()).thenReturn(expectedBlogs);

        ArrayList<String> response = blogController.getBlogs();

        assertEquals(expectedBlogs, response);
        verify(blogService, times(1)).getBlogs();
    }

    @Test
    void getBlogById_existingId() {
        int id = 1;
        Blog blog = new Blog();
        blog.setId(id);
        blog.setHtmlContent("<p>Test content</p>");

        when(blogService.getBlogById(id)).thenReturn(Optional.of(blog));

        ResponseEntity<Blog> expectedResponse = ResponseEntity.ok(blog);

        ResponseEntity<Blog> response = blogController.getBlogById(id);

        assertEquals(expectedResponse, response);
        verify(blogService, times(1)).getBlogById(id);
    }

    @Test
    void getBlogById_nonExistingId() {
        int id = 1;

        when(blogService.getBlogById(id)).thenReturn(Optional.empty());

        ResponseEntity<Blog> expectedResponse = ResponseEntity.notFound().build();

        ResponseEntity<Blog> response = blogController.getBlogById(id);

        assertEquals(expectedResponse, response);
        verify(blogService, times(1)).getBlogById(id);
    }
}
