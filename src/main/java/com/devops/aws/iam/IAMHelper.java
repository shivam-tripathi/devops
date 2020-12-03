package com.devops.aws.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AddRoleToInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.CreateInstanceProfileRequest;
import com.amazonaws.services.identitymanagement.model.InstanceProfile;

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

  public void createInstanceProfile(String profileName, String... roleNames) {
    AmazonIdentityManagement iamClient = IAMHelper.getClient();
    InstanceProfile profile = iamClient.createInstanceProfile(
            new CreateInstanceProfileRequest()
                    .withInstanceProfileName(profileName)
    ).getInstanceProfile();

    System.out.println(profile.getInstanceProfileName());

    for (String roleName : roleNames) {
      iamClient.addRoleToInstanceProfile(
              new AddRoleToInstanceProfileRequest()
                      .withInstanceProfileName(profileName)
                      .withRoleName(roleName)
      );
    }
  }
}
