package com.example.cloud_tracker.service;

import com.example.cloud_tracker.model.Blog;
import com.example.cloud_tracker.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BlogServiceTest {

  @Mock
  private BlogRepository blogRepository;

  @InjectMocks
  private BlogService blogService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void saveBlogTest() {
    String htmlContent = "<html><body>Test</body></html>";
    String title = "Test Title";

    Blog blog = new Blog();
    blog.setHtmlContent(htmlContent);
    blog.setTitle(title);

    when(blogRepository.save(blog)).thenReturn(blog);

    ResponseEntity<String> response = blogService.saveBlog(htmlContent, title);

    assertEquals(ResponseEntity.ok("Blog saved successfully"), response);
  }

  @Test
  void saveBlogNullHtmlContentTest() {
    assertThrows(IllegalArgumentException.class, () -> {
      blogService.saveBlog(null, "Test Title");
    });
  }

  @Test
  void saveBlogNullTitleTest() {
    assertThrows(IllegalArgumentException.class, () -> {
      blogService.saveBlog("<html><body>Test</body></html>", null);
    });
  }

  @Test
  void getBlogsTest() {
    List<Blog> expectedBlogs = new ArrayList<>();
    expectedBlogs.add(new Blog(1, "Title", "<html><body>Content</body></html>"));
    expectedBlogs.add(new Blog(2, "Title 2", "<html><body>Content 2</body></html>"));

    when(blogRepository.findAll()).thenReturn(expectedBlogs);

    List<Blog> actualBlogs = blogService.getBlogs();

    assertEquals(expectedBlogs, actualBlogs);
  }

  @Test
  void getBlogByIdTest() {
    int blogId = 1;
    Blog expectedBlog = new Blog(blogId, "Title", "<html><body>Content</body></html>");

    when(blogRepository.findById(blogId)).thenReturn(Optional.of(expectedBlog));

    Optional<Blog> actualBlogOptional = blogService.getBlogById(blogId);

    assertTrue(actualBlogOptional.isPresent());
    assertEquals(expectedBlog, actualBlogOptional.get());
  }

  @Test
  void getBlogByIdNotFoundTest() {
    int blogId = 1;

    when(blogRepository.findById(blogId)).thenReturn(Optional.empty());

    Optional<Blog> actualBlogOptional = blogService.getBlogById(blogId);

    assertFalse(actualBlogOptional.isPresent());
  }
}
