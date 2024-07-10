package com.example.cloud_tracker.service;

import static org.mockito.Mockito.*;

import com.example.cloud_tracker.model.MonthlyCost;
import com.example.cloud_tracker.repository.MonthlyCostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MonthlyCostServiceTest {

    @Mock
    private MonthlyCostRepository monthlyCostRepository;

    @InjectMocks
    private MonthlyCostService monthlyCostService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddNewMonthlyCost() {
        MonthlyCost monthlyCost = new MonthlyCost();
        monthlyCost.setDate("2023-06-01");
        monthlyCost.setService("Service1");
        monthlyCost.setCost(10.0);

        monthlyCostService.addNewMonthlyCost("2023-06-01", "Service1", 10.0);
        verify(monthlyCostRepository, times(1)).save(any(MonthlyCost.class));
    }
}
