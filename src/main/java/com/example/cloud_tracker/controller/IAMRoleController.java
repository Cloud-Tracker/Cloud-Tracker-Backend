package com.example.cloud_tracker.controller;

import com.example.cloud_tracker.dto.CostQueryDTO;
import com.example.cloud_tracker.dto.ServiceCostDTO;
import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.model.User;
import com.example.cloud_tracker.service.IAMRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
      @AuthenticationPrincipal User principal, @RequestBody IAMRole iamRole) {
    IAMRole newIAMRole = iamRoleService.addIAMRole(principal.getId(), iamRole);
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
  @GetMapping("/data")
  public ResponseEntity<CostQueryDTO> getData(@RequestParam String arn) {
      IAMRole iamRole = iamRoleService.getIAMRoleByArn(arn);
      return ResponseEntity.status(HttpStatus.OK).body(iamRoleService.getData(iamRole));
  }

  @GetMapping("/cost")
  public ResponseEntity<List<ServiceCostDTO>> getBlendedCost(CostQueryDTO costQueryDTO){
      List<ServiceCostDTO> blendedCost = iamRoleService.getBlendedCost(costQueryDTO);
      return ResponseEntity.status(HttpStatus.OK).body(blendedCost);
  }


}
