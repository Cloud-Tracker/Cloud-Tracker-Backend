package com.example.cloud_tracker.repository;

import com.example.cloud_tracker.model.MonthlyCost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyCostRepository extends JpaRepository <MonthlyCost, Integer> {

}
