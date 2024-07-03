package com.example.cloud_tracker.service;

import com.example.cloud_tracker.model.AWSService;
import com.example.cloud_tracker.repository.AWSServiceRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AWSServicesService {

  @Autowired private AWSServiceRepository awsServiceRepository;

  public List<AWSService> getServices() {
    return awsServiceRepository.findAll();
  }

  public String getAWSServiceByID(int id) {

    return awsServiceRepository.findById(id).get().getName();
  }

  public void addNewAWSService(int id, String name, Double cost) {
    AWSService awsService = new AWSService();
    awsService.setId(id);
    awsService.setName(name);
    awsService.setTotalCost(cost);
    awsServiceRepository.save(awsService);
  }
}
