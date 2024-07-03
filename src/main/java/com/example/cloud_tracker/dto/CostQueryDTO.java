package com.example.cloud_tracker.dto;

import com.amazonaws.auth.AWSCredentialsProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostQueryDTO {
  private String startDate;
  private String endDate;
  private AWSCredentialsProvider awsCredentialsProvider;
  private String region;
}
