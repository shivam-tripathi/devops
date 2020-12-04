package com.devops.aws.cloudwatch;

import static com.devops.aws.ec2.Ec2Instances.InstanceDetails;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricAlarmRequest;

import com.devops.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class CloudwatchAlarms {
  private static final int MESSAGE_THRESHOLD = 100;
  private static final int SLAVE_THRESHOLD = 2;
  private static final int MEMORY_THRESHOLD = 90;
  private static final int ROOT_DISK_THRESHOLD = 80;
  private static final int MONGO_DISK_THRESHOLD = 85;
  private static final int CPU_THRESHOLD = 30;

  @AllArgsConstructor
  @Getter
  @Setter
  @ToString
  public static class AlarmDescription {
    public String alarmName;
    public String description;
    public String metric;
    public String namespace;
    public List<Dimension> dimensionList;
    public double threshold;
    public String operator;
  }

  public void addAlarm(AlarmDescription desc) {
    Config.CloudwatchConfig config = Config.getConfig().getCloudwatch();
    AmazonCloudWatch cloudWatchClient = CloudwatchHelper.getClient();
    System.out.println(desc);
    cloudWatchClient.putMetricAlarm(
            new PutMetricAlarmRequest()
                    .withAlarmName(desc.alarmName)
                    .withAlarmDescription(desc.description)
                    .withOKActions(config.getOkActions())
                    .withAlarmActions(config.getAlarmActions())
                    .withMetricName(desc.metric)
                    .withNamespace(desc.namespace)
                    .withStatistic("Average")
                    .withDimensions(desc.dimensionList)
                    .withPeriod(60)
                    .withEvaluationPeriods(1)
                    .withThreshold(desc.threshold)
                    .withComparisonOperator(desc.operator)
                    .withTreatMissingData("breaching")
    );
  }

  public void addRamAlarm(InstanceDetails instanceDetails) {
    AlarmDescription desc = new AlarmDescription(
            String.format("%s-ram", instanceDetails.name),
            String.format("Memory usage is greater than %s on %s", MEMORY_THRESHOLD, instanceDetails.name),
            "UtilizedMemoryUncached",
            "EC2/Info",
            Collections.singletonList(new Dimension().withName("Machine").withValue(instanceDetails.name)),
            MEMORY_THRESHOLD,
            "GreaterThanOrEqualToThreshold"
    );
    addAlarm(desc);
  }

  public void addRootDiskAlarm(InstanceDetails instanceDetails) {
    AlarmDescription desc = new AlarmDescription(
            String.format("%s-root-disk", instanceDetails.name),
            String.format("Disk usage for / is greater than %s on %s", ROOT_DISK_THRESHOLD, instanceDetails.name),
            "UtilizedDisk-/",
            "EC2/Info",
            Collections.singletonList(new Dimension().withName("Machine").withValue(instanceDetails.name)),
            ROOT_DISK_THRESHOLD,
            "GreaterThanOrEqualToThreshold"
    );
    addAlarm(desc);
  }

  public void addCpuAlarm(InstanceDetails instanceDetails) {
    AlarmDescription desc = new AlarmDescription(
            String.format("%s-cpu", instanceDetails.name),
            String.format("CPU usage is greater than %s on %s", CPU_THRESHOLD, instanceDetails.name),
            "CPUUtilization",
            "AWS/EC2",
            Collections.singletonList(new Dimension().withName("InstanceId").withValue(instanceDetails.id)),
            CPU_THRESHOLD,
            "GreaterThanOrEqualToThreshold"
    );
    addAlarm(desc);
  }

  public void addMongoDiskAlarm(InstanceDetails instanceDetails) {
    AlarmDescription desc = new AlarmDescription(
            String.format("%s-mongo-disk", instanceDetails.name),
            String.format(
                    "Disk usage for /media/mon is greater than %s on %s", MONGO_DISK_THRESHOLD, instanceDetails.name
            ),
            "UtilizedDisk-/media/mon",
            "EC2/Info",
            Collections.singletonList(new Dimension().withName("Machine").withValue(instanceDetails.name)),
            MONGO_DISK_THRESHOLD,
            "GreaterThanOrEqualToThreshold"
    );
    addAlarm(desc);
  }

  public void addRedisAlarm(InstanceDetails instanceDetails) {
    AlarmDescription desc = new AlarmDescription(
            String.format("%s-slaves", instanceDetails.name),
            String.format("Slaves are less than %s on %s", SLAVE_THRESHOLD, instanceDetails.name),
            "Slaves",
            "EC2/Redis",
            Collections.singletonList(new Dimension().withName("Machine").withValue(instanceDetails.name)),
            SLAVE_THRESHOLD,
            "LessThanThreshold"
    );
    addAlarm(desc);
  }

  public void addRabbitAlarm(InstanceDetails instanceDetails) {
    List<String> queues = Config.getConfig().getInfra().getRabbit().getQueues();
    for (String queue : queues) {
      List<Dimension> dimensions = Arrays.asList(
              new Dimension().withName("Machine").withValue(instanceDetails.name),
              new Dimension().withName("Queue").withValue(queue)
      );
      AlarmDescription desc = new AlarmDescription(
              String.format("%s-messages", instanceDetails.name),
              String.format("Messages greater than %s on %s", MESSAGE_THRESHOLD, queue),
              "Total",
              "EC2/Rabbit",
              dimensions,
              MESSAGE_THRESHOLD,
              "GreaterThanOrEqualToThreshold"
      );
      addAlarm(desc);
    }
  }
}
