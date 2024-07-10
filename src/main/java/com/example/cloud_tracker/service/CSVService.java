package com.example.cloud_tracker.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CSVService {

  @Autowired private AWSServicesService awsServicesService;
  @Autowired private MonthlyCostService monthlyCostService;

  public void processCSV(MultipartFile file) throws IOException {
    try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

      List<String> serviceNames = new ArrayList<>();
      List<Double> totalCosts = new ArrayList<>();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      int idx = 0;
      for (CSVRecord record : records) {
        if (idx == 0) {
          extractServiceNames(record, serviceNames);
        } else if (idx == 1) {
          extractTotalCosts(record, totalCosts);
        } else {
          saveAWSServices(serviceNames, totalCosts);
          extractMonthlyCost(record, dateFormat);
        }
        idx++;
      }
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  void extractMonthlyCost(CSVRecord record, SimpleDateFormat dateFormat)
      throws ParseException {
    Date date = null;
    List<Double> costs = new ArrayList<>();
    for (int i = 0; i < record.size() - 1; i++) {
      if (i == 0) {
        date = dateFormat.parse(record.get(i));
      } else {
        if (record.get(i).isEmpty()) {
          costs.add(0.0);
        } else {
          costs.add(Double.parseDouble(record.get(i)));
        }
      }
    }
    SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = yearMonthDayFormat.format(date);

    for (int i = 0; i < costs.size(); i++)
      monthlyCostService.addNewMonthlyCost(
              formattedDate, awsServicesService.getAWSServiceByID(i + 1), costs.get(i));
  }

  void extractServiceNames(CSVRecord record, List<String> serviceNames) {
    for (int i = 1; i < record.size() - 1; i++) {
      String serviceName = record.get(i);
      serviceNames.add(serviceName.substring(0, serviceName.length() - 3));
    }
  }

  void extractTotalCosts(CSVRecord record, List<Double> totalCosts) {
    for (int i = 1; i < record.size() - 1; i++) {
      String totalCost = record.get(i);
      totalCosts.add(Double.parseDouble(totalCost));
    }
  }

  void saveAWSServices(List<String> serviceNames, List<Double> totalCosts) {
    for (int i = 0; i < serviceNames.size(); i++) {
      awsServicesService.addNewAWSService(i + 1, serviceNames.get(i), totalCosts.get(i));
    }
  }
}
