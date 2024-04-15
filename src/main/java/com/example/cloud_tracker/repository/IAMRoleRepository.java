package com.example.cloud_tracker.repository;

import com.example.cloud_tracker.model.IAMRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAMRoleRepository extends JpaRepository<IAMRole, String> {
    IAMRole findByAccountID(String accountID);

    List<IAMRole> findByUserId(int id);
}
