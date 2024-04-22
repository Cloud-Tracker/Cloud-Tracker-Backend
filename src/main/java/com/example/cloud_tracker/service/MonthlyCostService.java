package com.example.cloud_tracker.service;

import com.example.cloud_tracker.model.MonthlyCost;
import com.example.cloud_tracker.repository.MonthlyCostRepository;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonthlyCostService {
  @Autowired private MonthlyCostRepository monthlyCostRepository;

  public void addNewMonthlyCost(Date date, String serviceName, Double cost) {
    MonthlyCost monthlyCost = new MonthlyCost();
    monthlyCost.setDateOfCost(date);
    monthlyCost.setServiceName(serviceName);
    monthlyCost.setCost(cost);
    monthlyCostRepository.save(monthlyCost);
  }

  public List<MonthlyCost> getAllMonthlyCostInfo() {
    return monthlyCostRepository.findAll();
  }
}
