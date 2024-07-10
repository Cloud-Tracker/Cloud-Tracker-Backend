package com.example.cloud_tracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.cloud_tracker.model.AWSService;
import com.example.cloud_tracker.repository.AWSServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class AWSServicesServiceTest {

    @Mock
    private AWSServiceRepository awsServiceRepository;

    @InjectMocks
    private AWSServicesService awsServicesService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAWSServiceByID() {
        AWSService awsService = new AWSService();
        awsService.setId(1);
        awsService.setName("Service1");
        when(awsServiceRepository.findById(1)).thenReturn(Optional.of(awsService));

        String serviceName = awsServicesService.getAWSServiceByID(1);
        assertEquals("Service1", serviceName);
    }

    @Test
    public void testAddNewAWSService() {
        AWSService awsService = new AWSService();
        awsService.setId(1);
        awsService.setName("Service1");
        awsService.setTotalCost(100.0);

        awsServicesService.addNewAWSService(1, "Service1", 100.0);
        verify(awsServiceRepository, times(1)).save(any(AWSService.class));
    }
}
