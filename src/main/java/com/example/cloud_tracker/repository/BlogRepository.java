package com.example.cloud_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cloud_tracker.model.Blog;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {}
