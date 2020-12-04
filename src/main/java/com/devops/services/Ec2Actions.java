package com.devops.services;

import static com.devops.aws.ec2.Ec2Instances.InstanceDetails;

import com.devops.aws.cloudwatch.CloudwatchAlarms;
import com.devops.aws.ec2.Ec2Instances;

import java.util.Map;

public class Ec2Actions {
  public void addAlarm(Map<String, String> tags) {
    CloudwatchAlarms cloudwatchAlarms = new CloudwatchAlarms();
    new Ec2Instances()
            .describeInstances(tags)
            .stream()
            .map(InstanceDetails::new)
            .filter(InstanceDetails::isValidForAlarms)
            .forEach(instanceDetails -> {
              if ("mongo".equals(instanceDetails.type)) {
                cloudwatchAlarms.addMongoDiskAlarm(instanceDetails);
              }

              if ("rabbit".equals(instanceDetails.type) && instanceDetails.isMaster()) {
                cloudwatchAlarms.addRabbitAlarm(instanceDetails);
              }

              if ("redis".equals(instanceDetails.type) && instanceDetails.isMaster()) {
                cloudwatchAlarms.addRedisAlarm(instanceDetails);
              }

              cloudwatchAlarms.addRootDiskAlarm(instanceDetails);
              cloudwatchAlarms.addCpuAlarm(instanceDetails);
              cloudwatchAlarms.addRamAlarm(instanceDetails);
            });
  }
}
