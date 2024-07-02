package com.example.cloud_tracker.controller;

import com.amazonaws.services.securitytoken.model.AWSSecurityTokenServiceException;
import com.example.cloud_tracker.dto.Ec2DTO;
import com.example.cloud_tracker.dto.RIDTO;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.service.EC2InstanceService;
import com.example.cloud_tracker.service.IAMRoleService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class IAMRoleController {

  private static final String MOCK_ARN = "arn:aws:iam::123456789012:role/MockRole";
  private final IAMRoleService iamRoleService;
  private final EC2InstanceService ec2InstanceService;
  private final MockCostsService mockCostsService;

  public IAMRoleController(IAMRoleService iamRoleService, EC2InstanceService ec2InstanceService) {
    this.iamRoleService = iamRoleService;
    this.ec2InstanceService = ec2InstanceService;
    this.mockCostsService = new MockCostsService();
  }

  @PostMapping()
  public ResponseEntity<IAMRole> addRole(
      @AuthenticationPrincipal User principal, @RequestParam String arn) {
    IAMRole newIAMRole = iamRoleService.addIAMRole(principal.getId(), arn);
    return ResponseEntity.status(HttpStatus.CREATED).body(newIAMRole);
  }


  @GetMapping("/all")
  public ResponseEntity<List<IAMRole>> getRoles(@AuthenticationPrincipal User principal) {
    List<IAMRole> iamRoles = iamRoleService.getIAMRoles(principal.getId());
    return ResponseEntity.status(HttpStatus.OK).body(iamRoles);
  }
  @GetMapping
  public ResponseEntity<IAMRole> getRole(@RequestParam String arn) {
    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    return ResponseEntity.status(HttpStatus.OK).body(iamRole);
  }

  @GetMapping("/cost")
  public ResponseEntity<List<ServiceCostDTO>> getBlendedCost(@RequestParam String arn){
    // Check for predefined ARN
    if (MOCK_ARN.equals(arn)) {
      List<ServiceCostDTO> mockData = mockCostsService.generateRandomMockBlendedCost();
      return ResponseEntity.status(HttpStatus.OK).body(mockData);
    }

    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    try {
      List<ServiceCostDTO> blendedCost = iamRoleService.getBlendedCost(iamRole);
      return ResponseEntity.status(HttpStatus.OK).body(blendedCost);
    } catch (AWSSecurityTokenServiceException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @GetMapping("/ec2cost")
  public ResponseEntity<List<Ec2DTO>> getEc2Cost(@RequestParam String arn) {
    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    try {
      return ResponseEntity.status(HttpStatus.OK).body(iamRoleService.getEC2Data(iamRole));
    } catch (AWSSecurityTokenServiceException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }
  }
  @GetMapping("/forecast")
  public ResponseEntity<Double> getForecast(@RequestParam String arn){
    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    try {
      double predictedCost = iamRoleService.getForecast(iamRole);
      return ResponseEntity.status(HttpStatus.OK).body(predictedCost);
    } catch (AWSSecurityTokenServiceException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }
  @GetMapping("/offerings")
  public ResponseEntity<Map<Ec2DTO, List<RIDTO>>> getRIOfferings(@RequestParam String arn){
    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    try{
      return ResponseEntity.status(HttpStatus.OK).body(ec2InstanceService.getEc2FromRI(iamRole));
    }catch (AWSSecurityTokenServiceException ex){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

}
