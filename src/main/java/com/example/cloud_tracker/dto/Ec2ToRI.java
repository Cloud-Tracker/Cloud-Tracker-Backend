package com.example.cloud_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ec2ToRI {
    Ec2DTO instance;
    List<RIDTO> RIDTOS;
}
