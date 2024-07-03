package com.example.cloud_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCostDTO {
  private String date;
  private String service;
  private Double cost;
}
