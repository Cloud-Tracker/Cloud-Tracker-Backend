package com.example.cloud_tracker.repository;

import com.example.cloud_tracker.model.AWSService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AWSServiceRepository extends JpaRepository<AWSService, Integer> {}
