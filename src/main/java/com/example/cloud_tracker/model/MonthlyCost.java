package com.example.cloud_tracker.model;

import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "monthly-cost")
public class MonthlyCost {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private Date dateOfCost;
  private String serviceName;
  private Double cost;
}
