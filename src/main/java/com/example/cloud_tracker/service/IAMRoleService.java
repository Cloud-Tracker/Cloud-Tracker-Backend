package com.example.cloud_tracker.service;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.*;
import com.example.cloud_tracker.dto.CostQueryDTO;
import com.example.cloud_tracker.dto.Ec2DTO;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.repository.IAMRoleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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


    public List<Ec2DTO> getEC2Data(IAMRole iamRole){
        CostQueryDTO costQueryDTO = getData(iamRole);

        AWSCostExplorer client = AWSCostExplorerClientBuilder.standard()
                .withCredentials(costQueryDTO.getAwsCredentialsProvider())
                .build();

        List<Ec2DTO> ec2DTOS = new ArrayList<>();
        Expression filter = new Expression()
                .withDimensions(new DimensionValues()
                        .withKey(Dimension.SERVICE)
                        .withValues("AmazonEC2"));
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);

        GetCostAndUsageRequest request = new GetCostAndUsageRequest()
                .withTimePeriod(new DateInterval().withStart(String.valueOf(startDate)).withEnd(String.valueOf(endDate)))
                .withGranularity(Granularity.MONTHLY)
                .withMetrics("UnblendedCost", "UsageQuantity")
                .withFilter(filter)
                .withGroupBy(
                        new GroupDefinition().withType(GroupDefinitionType.DIMENSION).withKey("INSTANCE_TYPE"),
                        new GroupDefinition().withType(GroupDefinitionType.DIMENSION).withKey("REGION"),
                        new GroupDefinition().withType(GroupDefinitionType.DIMENSION).withKey("OPERATING_SYSTEM")
                );

        GetCostAndUsageResult result = client.getCostAndUsage(request);

        for (ResultByTime resultByTime : result.getResultsByTime()) {
            for (Group group : resultByTime.getGroups()) {
                String instanceType =  group.getKeys().get(0);
                String region =  group.getKeys().get(1);
                String os =  group.getKeys().get(2);
                String unblendedCost = group.getMetrics().get("UnblendedCost").getAmount();

                Ec2DTO ec2DTO = new Ec2DTO(instanceType, region, os, Integer.parseInt(unblendedCost));
                ec2DTOS.add(ec2DTO);
            }
        }
        return ec2DTOS;

    }

}
