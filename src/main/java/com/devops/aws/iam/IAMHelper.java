package com.devops.aws.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AddRoleToInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileRequest;

public class IAMHelper {
    private static AmazonIdentityManagement iamClient = null;

    public static AmazonIdentityManagement getClient() {
        if (iamClient == null) {
            iamClient = AmazonIdentityManagementClientBuilder
                .standard()
                .build();
        }
        return iamClient;
    }

    public void createInstanceProfile(String profileName, String ...roleNames) {
        AmazonIdentityManagement iamClient = IAMHelper.getClient();
        iamClient.createInstanceProfile(
            new CreateInstanceProfileRequest()
                .withInstanceProfileName(profileName)
        ).getInstanceProfile();

        for (String roleName: roleNames) {
            iamClient.addRoleToInstanceProfile(
                new AddRoleToInstanceProfileRequest()
                    .withInstanceProfileName(profileName)
                    .withRoleName(roleName)
            );
        }
    }
}
