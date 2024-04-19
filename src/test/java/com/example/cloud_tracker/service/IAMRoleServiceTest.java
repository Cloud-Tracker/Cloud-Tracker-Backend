package com.example.cloud_tracker.service;

import com.example.cloud_tracker.model.IAMRole;
import com.example.cloud_tracker.repository.IAMRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class IAMRoleServiceTest {

    @Mock
    private IAMRoleRepository iamRoleRepository;

    @InjectMocks
    private IAMRoleService iamRoleService;

    @Test
    void getIAMRoles_returnsListOfRoles() {
        int userId = 1;
        IAMRole role1 = new IAMRole(); // Mocked data
        IAMRole role2 = new IAMRole();
        List<IAMRole> expectedRoles = Arrays.asList(role1, role2);

        when(iamRoleRepository.findByUserId(userId)).thenReturn(expectedRoles);

        List<IAMRole> actualRoles = iamRoleService.getIAMRoles(userId);

        assertEquals(expectedRoles, actualRoles);
        verify(iamRoleRepository).findByUserId(userId);
    }

    @Test
    void getIAMRole_returnsRoleByAccountId() {
        String accountId = "12345";
        IAMRole expectedRole = new IAMRole();

        when(iamRoleRepository.findByAccountID(accountId)).thenReturn(expectedRole);

        IAMRole actualRole = iamRoleService.getIAMRole(accountId);

        assertEquals(expectedRole, actualRole);
        verify(iamRoleRepository).findByAccountID(accountId);
    }

    @Test
    void getIAMRoleByArn_returnsRoleByArn() {
        String arn = "arn:aws:iam::123456789012:role/SampleRole";
        IAMRole expectedRole = new IAMRole();

        when(iamRoleRepository.findByArn(arn)).thenReturn(expectedRole);

        IAMRole actualRole = iamRoleService.getIAMRoleByArn(arn);

        assertEquals(expectedRole, actualRole);
        verify(iamRoleRepository).findByArn(arn);
    }

    @Test
    void addIAMRole_savesAndReturnsRole() {
        int userId = 1;
        IAMRole newRole = new IAMRole();
        IAMRole savedRole = new IAMRole();

        when(iamRoleRepository.save(newRole)).thenReturn(savedRole);

        IAMRole result = iamRoleService.addIAMRole(userId, newRole);

        assertEquals(savedRole, result);
        verify(iamRoleRepository).save(newRole);
    }

}