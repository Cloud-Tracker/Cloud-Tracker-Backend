package com.example.cloud_tracker.service;

import com.example.cloud_tracker.controller.MockCostsService;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MockCostsServiceTest {

    private MockCostsService mockCostsService;

    @BeforeEach
    public void setUp() {
        mockCostsService = new MockCostsService();
    }

    @Test
    public void testGenerateRandomMockBlendedCost() {
        List<ServiceCostDTO> mockData = mockCostsService.generateRandomMockBlendedCost();
        assertNotNull(mockData);
        assertFalse(mockData.isEmpty());

        // Check if all dates are within the expected range (past 6 months from today)
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (ServiceCostDTO dto : mockData) {
            LocalDate date = LocalDate.parse(dto.getDate(), formatter);
            assertTrue(date.isAfter(sixMonthsAgo) || date.isEqual(sixMonthsAgo));
            assertTrue(date.isBefore(today) || date.isEqual(today));
        }

        // Check if all services in the mockData are valid services
        Set<String> validServices = Set.of(
                "AmazonEC2", "AmazonS3", "AmazonRDS", "AmazonLambda",
                "AmazonDynamoDB", "AmazonCloudFront", "AmazonEBS", "AmazonElastiCache"
        );
        Set<String> servicesInMockData = mockData.stream()
                .map(ServiceCostDTO::getService)
                .collect(Collectors.toSet());
        assertTrue(validServices.containsAll(servicesInMockData));

        // Check if costs are within expected ranges (between 10 and 500)
        for (ServiceCostDTO dto : mockData) {
            assertTrue(dto.getCost() >= 10 && dto.getCost() <= 500);
        }
    }

    @Test
    public void testGenerateRandomMockBlendedCostConsistency() {
        // Test to check if invoking generateRandomMockBlendedCost() multiple times returns consistent data
        List<ServiceCostDTO> mockData1 = mockCostsService.generateRandomMockBlendedCost();
        List<ServiceCostDTO> mockData2 = mockCostsService.generateRandomMockBlendedCost();

        assertEquals(mockData1.size(), mockData2.size());

        // Compare individual DTOs to ensure consistency (optional based on your needs)
        for (int i = 0; i < mockData1.size(); i++) {
            ServiceCostDTO dto1 = mockData1.get(i);
            ServiceCostDTO dto2 = mockData2.get(i);
            assertEquals(dto1.getDate(), dto2.getDate());
            assertEquals(dto1.getService(), dto2.getService());
            assertEquals(dto1.getCost(), dto2.getCost());
        }
    }


}
