package com.example.cloud_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AWS_services")
public class AWSService{
  @Id private int id;
  private String name;
  private double totalCost;
}
