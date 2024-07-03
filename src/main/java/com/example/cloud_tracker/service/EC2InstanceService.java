package com.example.cloud_tracker.service;

import com.example.cloud_tracker.dto.Ec2DTO;
import com.example.cloud_tracker.dto.Ec2ToRI;
import com.example.cloud_tracker.dto.RIDTO;
import com.example.cloud_tracker.model.IAMRole;
import java.util.*;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

@Service
public class EC2InstanceService {

    private final AWSServicesService awsServicesService;
    IAMRoleService iamRoleService;

    public EC2InstanceService(AWSServicesService awsServicesService, IAMRoleService iamRoleService){
        this.awsServicesService = awsServicesService;
        this.iamRoleService = iamRoleService;
    }

    public List<Ec2ToRI> getEc2FromRI(IAMRole iamRole){
        List<Ec2DTO> ec2DTOS = iamRoleService.getEC2Data(iamRole);
        Ec2Client ec2 = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        DescribeReservedInstancesOfferingsRequest request = DescribeReservedInstancesOfferingsRequest.builder()
                    .build();
        DescribeReservedInstancesOfferingsResponse response = ec2.describeReservedInstancesOfferings(request);
        List<Ec2ToRI> instancesOfferings = new ArrayList<>();
//        System.out.println(ec2DTOS.size());

        for (Ec2DTO ec2DTO : ec2DTOS){
            Ec2ToRI ec2ToRI = new Ec2ToRI();
            ec2ToRI.setInstance(ec2DTO);
            List<RIDTO> ridtos = new ArrayList<>();
            for(ReservedInstancesOffering offering : response.reservedInstancesOfferings()){
                if(Objects.equals(offering.instanceTypeAsString(), ec2DTO.getInstanceType()) &&
                    Objects.equals(String.valueOf(offering.productDescription()), ec2DTO.getOS())){
                    String az = offering.availabilityZone();
                    String offeringRegion = ec2DTO.getRegion();
                    if(az != null)
                        offeringRegion = az.substring(0, az.length() - 1);
                    if (offeringRegion.equals(ec2DTO.getRegion())) {
                        RIDTO ridto = new RIDTO();
                        ridto.setInstanceType(offering.instanceTypeAsString());
                        // Price per hour
                        ridto.setPrice(offering.fixedPrice());
                        ridto.setDuration(offering.duration());
                        ridto.setOS(ec2DTO.getOS());
                        ridtos.add(ridto);
                    }

                }
            }
            if(ridtos.isEmpty())
                continue;
//            System.out.println(ridtos);
            ec2ToRI.setRIDTOS(ridtos);
            instancesOfferings.add(ec2ToRI);
        }

        return instancesOfferings;
    }
}
