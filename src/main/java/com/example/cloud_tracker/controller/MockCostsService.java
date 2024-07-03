package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.dto.ServiceCostDTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MockCostsService {
    private List<ServiceCostDTO> mockData;

    public List<ServiceCostDTO> generateRandomMockBlendedCost() {
        if (mockData != null)
            return mockData;

        mockData = new ArrayList<>();
        String[] services = {
                "AmazonEC2", "AmazonS3", "AmazonRDS", "AmazonLambda",
                "AmazonDynamoDB", "AmazonCloudFront", "AmazonEBS", "AmazonElastiCache"
        };

        LocalDate startDate = LocalDate.now().minusMonths(6);
        LocalDate endDate = LocalDate.now();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (long i = 0; i <= daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            String date = currentDate.format(formatter);

            // Random number of services each day (between 1 and the number of available services)
            int numServices = 1 + random.nextInt(services.length);

            // Randomly select services
            Set<String> selectedServices = new HashSet<>();
            while (selectedServices.size() < numServices) {
                int index = random.nextInt(services.length);
                selectedServices.add(services[index]);
            }

            for (String service : selectedServices) {
                double cost = 10 + (500 - 10) * random.nextDouble(); // Random cost between 10 and 500
                ServiceCostDTO serviceCostDTO = new ServiceCostDTO(date, service, cost);
                mockData.add(serviceCostDTO);
            }
        }

        return mockData;
    }
}
