package com.example.cloud_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cloud_tracker.model.BlackListedTokens;

@Repository
public interface BlackListedTokensRepository extends JpaRepository<BlackListedTokens,Integer> {
    Boolean existsByToken(String token);
}
