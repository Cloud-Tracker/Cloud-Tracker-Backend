package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.model.AWSService;
import com.example.cloud_tracker.model.MonthlyCost;
import com.example.cloud_tracker.service.AWSServicesService;
import com.example.cloud_tracker.service.CSVService;
import com.example.cloud_tracker.service.MonthlyCostService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cost-info")
public class CSVController {
  @Autowired private CSVService csvService;
  @Autowired private AWSServicesService awsServicesService;
  @Autowired private MonthlyCostService monthlyCostService;

  @PostMapping("/upload")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Please upload a file");
    }
    try {
      csvService.processCSV(file);
      return ResponseEntity.ok().body("File uploaded successfully");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to upload file: " + e.getMessage());
    }
  }

  @GetMapping("/services")
  public ResponseEntity<List<AWSService>> getAllServices() {
    List<AWSService> services = awsServicesService.getServices();
    return ResponseEntity.ok().body(services);
  }

  @GetMapping("/months")
  public ResponseEntity<List<MonthlyCost>> getAllMonthsCostInfo() {
    List<MonthlyCost> monthlyCosts = monthlyCostService.getAllMonthlyCostInfo();
    return ResponseEntity.ok().body(monthlyCosts);
  }
}
