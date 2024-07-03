package com.example.cloud_tracker.service;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.*;
import com.example.cloud_tracker.dto.CostQueryDTO;
import com.example.cloud_tracker.dto.Ec2DTO;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.repository.IAMRoleRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class IAMRoleService {

    private final IAMRoleRepository iamRoleRepository;


    public IAMRoleService(IAMRoleRepository iamRoleRepository) {
        this.iamRoleRepository = iamRoleRepository;
    }

    public List<IAMRole> getIAMRoles(int id) {
        return iamRoleRepository.findByUserId(id);
    }
    public IAMRole getIAMRole(String accountID) {
        return iamRoleRepository.findByAccountID(accountID);
    }
    public IAMRole getIAMRoleByArn(String arn) {
        return iamRoleRepository.findByArn(arn);
    }
    public IAMRole addIAMRole(int id, String arn) {
        IAMRole newIAMRole = new IAMRole(arn);
        System.out.println(id);
        newIAMRole.setUserId(id);
        return iamRoleRepository.save(newIAMRole);
    }

    public CostQueryDTO getData(IAMRole iamRole) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        AWSCredentialsProvider credentialsProvider = new STSAssumeRoleSessionCredentialsProvider.Builder(
                    iamRole.getArn(), "SESSION_NAME")
                    .build();
        return new CostQueryDTO( startDate.toString(), endDate.toString(), credentialsProvider, "us-east-1");
    }

    public List<ServiceCostDTO> getBlendedCost(IAMRole iamRole) {
        
        CostQueryDTO costQueryDTO = getData(iamRole);
        AWSCostExplorer client = AWSCostExplorerClientBuilder.standard()
                .withCredentials(costQueryDTO.getAwsCredentialsProvider())
                .withRegion(costQueryDTO.getRegion())
                .build();
 
        GetCostAndUsageRequest request = new GetCostAndUsageRequest()
                .withTimePeriod(new DateInterval()
                        .withStart(costQueryDTO.getStartDate())
                        .withEnd(costQueryDTO.getEndDate()))
                .withGranularity("DAILY")
                .withMetrics("BlendedCost")
                .withGroupBy(new GroupDefinition().withType("DIMENSION").withKey("SERVICE"));
 
        GetCostAndUsageResult result = client.getCostAndUsage(request);
 
        List<ServiceCostDTO> totalBlendedCost = new ArrayList<>();
 
        for (ResultByTime resultByTime : result.getResultsByTime()) {
            String date = resultByTime.getTimePeriod().getStart();
            for(Group group : resultByTime.getGroups()){
                String service = group.getKeys().get(0);
                if (group.getMetrics() != null) {
                    MetricValue blendedCostMetric = group.getMetrics().get("BlendedCost");
                    if (blendedCostMetric != null) {
                        String currentBlendedCost = blendedCostMetric.getAmount();
                        ServiceCostDTO serviceCostDTO = new ServiceCostDTO(date, service,Double.parseDouble(currentBlendedCost));
                        totalBlendedCost.add(serviceCostDTO);
                    }
                    else{
                        ServiceCostDTO serviceCostDTO = new ServiceCostDTO(date, service,0.0);
                        totalBlendedCost.add(serviceCostDTO);
                    }
                }
                else{
                    ServiceCostDTO serviceCostDTO = new ServiceCostDTO(date, service,0.0);
                    totalBlendedCost.add(serviceCostDTO);
                }
            }
        }
        return totalBlendedCost;
    }


    public List<Ec2DTO> getEC2Data(IAMRole iamRole) {
        CostQueryDTO costQueryDTO = getData(iamRole);

        AWSCostExplorer client = AWSCostExplorerClientBuilder.standard()
                .withCredentials(costQueryDTO.getAwsCredentialsProvider())
                .withRegion(Regions.US_EAST_1)
                .build();

        List<Ec2DTO> ec2DTOS = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusMonths(12);
        LocalDate endDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String start = startDate.format(formatter);
        String end = endDate.format(formatter);

        GetCostAndUsageRequest request = new GetCostAndUsageRequest()
                .withTimePeriod(new DateInterval().withStart(start).withEnd(end))
                .withGranularity(Granularity.MONTHLY)
                .withMetrics("UnblendedCost")
                .withGroupBy(
                        new GroupDefinition().withType("DIMENSION").withKey("INSTANCE_TYPE"),
                        new GroupDefinition().withType("DIMENSION").withKey("REGION")

                );

        GetCostAndUsageResult result = client.getCostAndUsage(request);


        for (ResultByTime resultByTime : result.getResultsByTime()) {
            for (Group group : resultByTime.getGroups()) {
                List<String> keys = group.getKeys();
                String instanceType = !keys.isEmpty() ? keys.get(0) : "Unknown InstanceType";
                String region = keys.size() > 1 ? keys.get(1) : "Unknown Region";


                if (!Objects.equals(instanceType, "NoInstanceType")) {
                    GetCostAndUsageRequest additionalRequest = new GetCostAndUsageRequest()
                            .withTimePeriod(new DateInterval().withStart(start).withEnd(end))
                            .withGranularity(Granularity.MONTHLY)
                            .withMetrics("UnblendedCost")
                            .withFilter(new Expression()
                                    .withAnd(Arrays.asList(
                                            new Expression().withDimensions(new DimensionValues().withKey("INSTANCE_TYPE").withValues(instanceType)),
                                            new Expression().withDimensions(new DimensionValues().withKey("REGION").withValues(region))
                                    )))
                            .withGroupBy(
                                    new GroupDefinition().withType("DIMENSION").withKey("OPERATING_SYSTEM")
                            );

                    GetCostAndUsageResult additionalResult = client.getCostAndUsage(additionalRequest);

                    for (ResultByTime additionalResultByTime : additionalResult.getResultsByTime()) {
                        for (Group additionalGroup : additionalResultByTime.getGroups()) {
                            String usageType = additionalGroup.getKeys().get(0);
                            double additionalCost = Double.parseDouble(additionalGroup.getMetrics().get("UnblendedCost").getAmount());

                            Ec2DTO ec2DTO = new Ec2DTO(instanceType, region, usageType, additionalCost);
                            ec2DTOS.add(ec2DTO);
                        }
                    }
                }
            }
        }
        ec2DTOS.add(new Ec2DTO("m1.small", "us-east-1", "Linux/UNIX", 5.3));
        return ec2DTOS;
    }

    public double getForecast(IAMRole iamRole) {
        CostQueryDTO costQueryDTO = getData(iamRole);
        AWSCostExplorer costExplorer = AWSCostExplorerClientBuilder.standard()
                .withCredentials(costQueryDTO.getAwsCredentialsProvider())
                .withRegion(costQueryDTO.getRegion())
                .build();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        DateInterval dateInterval = new DateInterval()
                .withStart(startDate.format(formatter))
                .withEnd(endDate.format(formatter));

        GetCostForecastRequest request = new GetCostForecastRequest()
                .withTimePeriod(dateInterval)
                .withMetric(Metric.BLENDED_COST)
                .withGranularity("MONTHLY");

        GetCostForecastResult result = costExplorer.getCostForecast(request);

        return Double.parseDouble(result.getTotal().getAmount());
    }

}
