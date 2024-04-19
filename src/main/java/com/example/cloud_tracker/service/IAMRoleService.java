package com.example.cloud_tracker.service;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.STSAssumeRoleSessionCredentialsProvider;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.repository.IAMRoleRepository;

import java.time.LocalDate;
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
    public String getData(IAMRole iamRole) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);
        AWSCredentialsProvider credentialsProvider = new STSAssumeRoleSessionCredentialsProvider.Builder(
                iamRole.getArn(), "SESSION_NAME")
                .withExternalId("ROFL")

                .build();
        // Query
//        System.out.println("Data from " + startDate + " to " + endDate + " using " + credentialsProvider.getCredentials());
        return "Data from " + startDate + " to " + endDate + " using " + credentialsProvider.getCredentials();
    }
}
