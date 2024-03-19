package com.example.cloud_tracker.repository;

import com.example.cloud_tracker.model.IAMRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAMRoleRepository extends JpaRepository<IAMRole, Long>{
    IAMRole findByAccountID(String accountID);
    List<IAMRole> findByUserId(int id);
}
