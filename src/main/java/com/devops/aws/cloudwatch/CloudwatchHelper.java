package com.devops.aws.cloudwatch;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;

public class CloudwatchHelper {
  private static AmazonCloudWatch cloudWatchClient = null;

  public static AmazonCloudWatch getClient() {
    if (cloudWatchClient == null) {
      cloudWatchClient = AmazonCloudWatchClientBuilder.defaultClient();
    }
    return cloudWatchClient;
  }
}
