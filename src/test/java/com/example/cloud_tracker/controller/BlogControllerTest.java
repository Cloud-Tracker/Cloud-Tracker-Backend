package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.dto.BlogDTO;
import com.example.cloud_tracker.model.Blog;
import com.example.cloud_tracker.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
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
    void saveBlogTest() {
        BlogDTO blogDTO = new BlogDTO();
        blogDTO.setHtmlContent("<html><body>Test</body></html>");
        blogDTO.setTitle("Test Title");

        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Success", HttpStatus.OK);

        when(blogService.saveBlog(blogDTO.getHtmlContent(), blogDTO.getTitle())).thenReturn(expectedResponse);

        ResponseEntity<String> actualResponse = blogController.saveBlog(blogDTO);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBlogsTest() {
        List<Blog> expectedBlogs = new ArrayList<>();
        expectedBlogs.add(new Blog(1, "Title", "<html><body>Content</body></html>"));

        when(blogService.getBlogs()).thenReturn(expectedBlogs);

        List<Blog> actualBlogs = blogController.getBlogs();

        assertEquals(expectedBlogs, actualBlogs);
    }

    @Test
    void getBlogByIdTest() {
        int blogId = 1;
        Blog expectedBlog = new Blog(blogId, "Title", "<html><body>Content</body></html>");

        when(blogService.getBlogById(blogId)).thenReturn(Optional.of(expectedBlog));

        ResponseEntity<Blog> expectedResponse = ResponseEntity.ok(expectedBlog);

        ResponseEntity<Blog> actualResponse = blogController.getBlogById(blogId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void getBlogByIdNotFoundTest() {
        int blogId = 1;

        when(blogService.getBlogById(blogId)).thenReturn(Optional.empty());

        ResponseEntity<Blog> expectedResponse = ResponseEntity.notFound().build();

        ResponseEntity<Blog> actualResponse = blogController.getBlogById(blogId);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testUpdateBlog() {
        int id = 1;
        String htmlContent = "<p>Updated content</p>";
        String title = "Updated Title";
        BlogDTO content = new BlogDTO(htmlContent, title);

        when(blogService.updateBlog(id, htmlContent, title))
                .thenReturn(new ResponseEntity<>("Blog updated successfully", HttpStatus.OK));

        ResponseEntity<String> responseEntity = blogController.updateBlog(id, content);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Blog updated successfully", responseEntity.getBody());

        verify(blogService, times(1)).updateBlog(id, htmlContent, title);
    }

    @Test
    public void testDeleteBlog() {
        int id = 1;

        when(blogService.deleteBlog(id)).thenReturn(new ResponseEntity<>("Blog deleted successfully", HttpStatus.OK));

        ResponseEntity<String> responseEntity = blogController.deleteBlog(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Blog deleted successfully", responseEntity.getBody());

        verify(blogService, times(1)).deleteBlog(id);
    }
}
