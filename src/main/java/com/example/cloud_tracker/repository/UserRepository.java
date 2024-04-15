package com.example.cloud_tracker.repository;

import com.example.cloud_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  User findByEmail(String email);

  User findById(int id);
}
