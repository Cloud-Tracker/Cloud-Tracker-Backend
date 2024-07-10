package com.example.cloud_tracker.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CSVServiceTest {

    @Mock
    private AWSServicesService awsServicesService;

    @Mock
    private MonthlyCostService monthlyCostService;

    @InjectMocks
    private CSVService csvService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessCSV() throws IOException, ParseException {
        MultipartFile file = mock(MultipartFile.class);
        String csvContent = "Service,EC2-Instances($),Cost Explorer($),Tax($),Secrets Manager($),EC2-Other($),S3($),CloudShell($),Amplify($),Elastic Container Registry Public($),VPC($),CloudWatch($),Key Management Service($),Total costs($)\n" +
                "Service total,0.845933,0.19,0.15,0.097467,0.000007,1.776500e-06,4.670000e-08,0,0,0.0,0,0.0,1.283408\n" +
                "2023-10-01,0.845933,,0.13,0.076882,0.000007,2.897000e-07,,0,0,0.0,0,0.0,1.052821\n";
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes()));

        csvService.processCSV(file);

        verify(awsServicesService, times(1)).addNewAWSService(1, "EC2-Instances", 0.845933);
        verify(awsServicesService, times(1)).addNewAWSService(2, "Cost Explorer", 0.19);
        verify(awsServicesService, times(1)).addNewAWSService(3, "Tax", 0.15);
        verify(monthlyCostService, times(1)).addNewMonthlyCost("2023-10-01", awsServicesService.getAWSServiceByID(1), 0.845933);
        verify(monthlyCostService, times(1)).addNewMonthlyCost("2023-10-01", awsServicesService.getAWSServiceByID(3), 0.13);
    }

    @Test
    public void testExtractMonthlyCost() throws ParseException, IOException {
        String[] recordArray = {"2023-10-01", "0.845933", "", "0.13", "0.076882", "0.000007", "2.897000e-07", "", "0", "0", "0.0", "0", "0.0"};
        CSVRecord record = CSVFormat.DEFAULT.parse(new StringReader(String.join(",", recordArray))).iterator().next();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        csvService.extractMonthlyCost(record, dateFormat);

        verify(monthlyCostService, times(1)).addNewMonthlyCost("2023-10-01", awsServicesService.getAWSServiceByID(1), 0.845933);
        verify(monthlyCostService, times(1)).addNewMonthlyCost("2023-10-01", awsServicesService.getAWSServiceByID(3), 0.13);
    }

    @Test
    public void testExtractServiceNames() throws IOException {
        String[] recordArray = {"Service", "EC2-Instances($)", "Cost Explorer($)", "Tax($)", "Secrets Manager($)", "EC2-Other($)", "S3($)", "CloudShell($)", "Amplify($)", "Elastic Container Registry Public($)", "VPC($)", "CloudWatch($)", "Key Management Service($)", "Total costs($)"};
        CSVRecord record = CSVFormat.DEFAULT.parse(new StringReader(String.join(",", recordArray))).iterator().next();
        List<String> serviceNames = new ArrayList<>();

        csvService.extractServiceNames(record, serviceNames);

        assertEquals(Arrays.asList("EC2-Instances", "Cost Explorer", "Tax", "Secrets Manager", "EC2-Other", "S3", "CloudShell", "Amplify", "Elastic Container Registry Public", "VPC", "CloudWatch", "Key Management Service"), serviceNames);
    }

    @Test
    public void testExtractTotalCosts() throws IOException {
        String[] recordArray = {"Service total", "0.845933", "0.19", "0.15", "0.097467", "0.000007", "1.776500e-06", "4.670000e-08", "0", "0", "0.0", "0"};
        CSVRecord record = CSVFormat.DEFAULT.parse(new StringReader(String.join(",", recordArray))).iterator().next();
        List<Double> totalCosts = new ArrayList<>();

        csvService.extractTotalCosts(record, totalCosts);

        assertEquals(Arrays.asList(0.845933, 0.19, 0.15, 0.097467, 0.000007, 1.776500e-06, 4.670000e-08, 0.0, 0.0, 0.0), totalCosts);
    }

    @Test
    public void testSaveAWSServices() {
        List<String> serviceNames = Arrays.asList("EC2-Instances", "Cost Explorer", "Tax", "Secrets Manager", "EC2-Other", "S3", "CloudShell", "Amplify", "Elastic Container Registry Public", "VPC");
        List<Double> totalCosts = Arrays.asList(0.845933, 0.19, 0.15, 0.097467, 0.000007, 1.776500e-06, 4.670000e-08, 0.0, 0.0, 0.0);

        csvService.saveAWSServices(serviceNames, totalCosts);

        verify(awsServicesService, times(1)).addNewAWSService(1, "EC2-Instances", 0.845933);
        verify(awsServicesService, times(1)).addNewAWSService(2, "Cost Explorer", 0.19);
        verify(awsServicesService, times(1)).addNewAWSService(3, "Tax", 0.15);
    }
}
