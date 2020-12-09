package com.devops.aws.ssm;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;

public class SsmHelper {
  private static AWSSimpleSystemsManagement ssmClient;
  public static AWSSimpleSystemsManagement getClient() {
    if (ssmClient == null) {
      ssmClient = AWSSimpleSystemsManagementClientBuilder.defaultClient();
    }
    return ssmClient;
  }
}
