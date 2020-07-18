package com.devops.aws.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;

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
}
