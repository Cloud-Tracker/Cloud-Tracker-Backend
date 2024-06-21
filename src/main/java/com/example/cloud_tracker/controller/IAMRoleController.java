package com.example.cloud_tracker.controller;

import com.amazonaws.services.securitytoken.model.AWSSecurityTokenServiceException;
import com.example.cloud_tracker.dto.CostQueryDTO;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.service.IAMRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/role")
public class IAMRoleController {

  private final IAMRoleService iamRoleService;

  public IAMRoleController(IAMRoleService iamRoleService) {
    this.iamRoleService = iamRoleService;
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
    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    try {
      List<ServiceCostDTO> blendedCost = iamRoleService.getBlendedCost(iamRole);
      return ResponseEntity.status(HttpStatus.OK).body(blendedCost);
    } catch (AWSSecurityTokenServiceException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

  @GetMapping("/forecast")
  public ResponseEntity<Integer> getForecast(@RequestParam String arn){
    IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
    try {
      Integer predictedCost = iamRoleService.getForecast(iamRole);
      return ResponseEntity.status(HttpStatus.OK).body(predictedCost);
    } catch (AWSSecurityTokenServiceException ex) {
      ex.printStackTrace();
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
  }

}
