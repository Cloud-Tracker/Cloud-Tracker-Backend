package com.example.cloud_tracker.repository;

import com.example.cloud_tracker.model.IAMRole;
import init.IAMRoleInit;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest

public class IAMRoleRepositoryTest {

    @Autowired
    private IAMRoleRepository iamRoleRepository;

    @Test
    public void testFindIAMRoleByAccountID() {
        IAMRole iamRole = IAMRoleInit.createIAMRole();
        iamRoleRepository.save(iamRole);

        IAMRole found = iamRoleRepository.findByAccountID(iamRole.getAccountID());
        Assertions.assertThat(found.getAccountID()).isEqualTo(iamRole.getAccountID());
    }

    @Test
    public void testFindIAMRoleByUserId() {
        IAMRole iamRole = IAMRoleInit.createIAMRole();
        iamRoleRepository.save(iamRole);

        List<IAMRole> found = iamRoleRepository.findByUserId(iamRole.getUserId());
        Assertions.assertThat(found.get(0).getUserId()).isEqualTo(iamRole.getUserId());
    }
    @Test
    public void testFindIAMRoleByArn() {
        IAMRole iamRole = IAMRoleInit.createIAMRole();
        iamRoleRepository.save(iamRole);

        IAMRole found = iamRoleRepository.findByArn(iamRole.getArn());
        Assertions.assertThat(found.getArn()).isEqualTo(iamRole.getArn());
    }
}
