package com.example.cloud_tracker.service;


import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.repository.IAMRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IAMRoleService {

    private final IAMRoleRepository iamRoleRepository;

    public IAMRoleService(IAMRoleRepository iamRoleRepository) {
        this.iamRoleRepository = iamRoleRepository;
    }

    public List<IAMRole> getIAMRoles(int id) {
        return iamRoleRepository.findByUserId(id);
    }

    public IAMRole addIAMRole(int id, IAMRole iamRole) {
        iamRole.setUserId(id);
        return iamRoleRepository.save(iamRole);
    }
}
