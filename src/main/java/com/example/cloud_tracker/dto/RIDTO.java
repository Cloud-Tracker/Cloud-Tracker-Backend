package com.example.cloud_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RIDTO {
  String instanceType;
  Long duration;
  Float price;
  String OS;
}
