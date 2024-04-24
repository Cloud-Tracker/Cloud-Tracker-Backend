package com.example.cloud_tracker.service;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.Group;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.amazonaws.services.costexplorer.model.MetricValue;
import com.amazonaws.services.costexplorer.model.ResultByTime;
import com.example.cloud_tracker.dto.CostQueryDTO;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.repository.IAMRoleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
    public IAMRole addIAMRole(int id, IAMRole iamRole) {
        iamRole.setUserId(id);
        return iamRoleRepository.save(iamRole);
    }
    public CostQueryDTO getData(IAMRole iamRole) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(6);
        AWSCredentialsProvider credentialsProvider = new STSAssumeRoleSessionCredentialsProvider.Builder(
                iamRole.getArn(), "SESSION_NAME")
                .withExternalId("ROFL")

                .build();
        // Query
//        System.out.println("Data from " + startDate + " to " + endDate + " using " + credentialsProvider.getCredentials());
        CostQueryDTO costQueryDTO = new CostQueryDTO( startDate.toString(), endDate.toString(), credentialsProvider, "us-east-1");
        return costQueryDTO;
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

}
