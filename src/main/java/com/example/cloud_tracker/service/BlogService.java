package com.example.cloud_tracker.service;

import com.example.cloud_tracker.model.Blog;
import com.example.cloud_tracker.repository.BlogRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public ResponseEntity<String> saveBlog(String htmlContent, String title) {
        if (htmlContent == null)
            throw new IllegalArgumentException("htmlContent cannot be null");

        if (title == null)
            throw new IllegalArgumentException("title cannot be null");

        Blog blog = new Blog();
        blog.setHtmlContent(htmlContent);
        blog.setTitle(title);
        blogRepository.save(blog);
        return ResponseEntity.ok("Blog saved successfully");
    }

    public List<Blog> getBlogs() {

        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(int id) {
        return blogRepository.findById(id);
    }

    public ResponseEntity<String> updateBlog(int id, String htmlContent, String title) {
        if (htmlContent == null)
            throw new IllegalArgumentException("htmlContent cannot be null");

        if (title == null)
            throw new IllegalArgumentException("title cannot be null");

        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            Blog blog = optionalBlog.get();
            blog.setHtmlContent(htmlContent);
            blog.setTitle(title);
            blogRepository.save(blog);
            return ResponseEntity.ok("Blog updated successfully");
        }

        throw new IllegalArgumentException("Blog not found");

    }

    public ResponseEntity<String> deleteBlog(int id) {
        Optional<Blog> optionalBlog = blogRepository.findById(id);
        if (optionalBlog.isPresent()) {
            blogRepository.deleteById(id);
            return ResponseEntity.ok("Blog deleted successfully");
        }

        throw new IllegalArgumentException("Blog not found");
    }
}
