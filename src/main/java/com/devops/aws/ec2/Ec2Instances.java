package com.devops.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.ToString;


public class Ec2Instances {
  @AllArgsConstructor
  @ToString
  public static class InstanceDetails {
    public String id;
    public String ip;
    public String privateIp;
    public String name;
    public String type;
    public String role;
    public String env;
    Map<String, String> tags;

    public InstanceDetails(Instance instance) {
      tags = instance.getTags().stream().collect(Collectors.toMap(Tag::getKey, Tag::getValue));
      id = instance.getInstanceId();
      ip = instance.getPublicIpAddress();
      privateIp = instance.getPrivateIpAddress();
      name = tags.get("Name");
      type = tags.get("type");
      role = tags.get("role");
      env = tags.get("env");
    }

    public boolean isValidForAlarms() {
      return ip != null && name != null && "prod".equals(env) && type != null && !"none".equals(type);
    }

    public boolean isMaster() {
      return "master".equals(role);
    }
  }

  public List<Instance> describeAllInstances() {
    return describeInstances(new HashMap<>());
  }

  public List<Instance> describeInstances(Map<String, String> tags) {
    AmazonEC2 client = Ec2Helper.getClient();
    String token;
    List<Instance> instances = new ArrayList<>();
    DescribeInstancesRequest request = new DescribeInstancesRequest();
    if (tags != null && tags.size() != 0) {
      List<Filter> filters = new ArrayList<>();
      tags.forEach((key, value) -> filters.add(new Filter().withName("tag:" + key).withValues(value)));
      request.withFilters(filters);
    }
    do {
      DescribeInstancesResult iterRes = client.describeInstances(request);
      token = iterRes.getNextToken();
      request.setNextToken(token);
      iterRes.getReservations().forEach(reservation -> instances.addAll(reservation.getInstances()));
    } while (token != null);

    return instances;
  }

  public List<Instance> describeInstancesWithTagList(Map<String, List<String>> tags) {
    AmazonEC2 client = Ec2Helper.getClient();
    List<Instance> instances = new ArrayList<>();
    DescribeInstancesRequest request = new DescribeInstancesRequest();
    if (tags != null && tags.size() != 0) {
      List<Filter> filters = new ArrayList<>();
      tags.forEach((key, values) -> {
        values.forEach(value -> {
          filters.add(new Filter().withName("tag:" + key).withValues(values));
        });
      });
      request.setFilters(filters);
    }
    DescribeInstancesResult result;
    do {
      result = client.describeInstances(request);
      request.setNextToken(result.getNextToken());
      result.getReservations().forEach(reservation -> instances.addAll(reservation.getInstances()));
    } while (result.getNextToken() != null);
    return instances;
  }

  public void addTagsToAllVolumes(String instanceId) {
    Ec2Volumes ec2VolumesHelper = new Ec2Volumes();
    DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
    Ec2Helper.getClient().describeInstances(describeInstancesRequest).getReservations().forEach(reservation -> {
      reservation.getInstances().forEach(instance -> {
        System.out.println(instance);
        instance.getBlockDeviceMappings().forEach(instanceBlockDeviceMapping -> {
          String volumeId = instanceBlockDeviceMapping.getEbs().getVolumeId();
          System.out.println(volumeId);
          ec2VolumesHelper.addTagsToVolumeById(volumeId, instance.getTags());
        });
      });
    });
  }

  public void launchInstance(
          String imageId,
          String instanceType,
          String subnetId,
          String instanceProfileArn,
          String rootDiskDeviceName,
          int rootVolumeSize,
          String keyName,
          List<String> securityGroupsIds,
          Map<String, String> tags
  ) {
    List<Tag> ec2Tags = tags
            .entrySet()
            .stream()
            .map((e) -> new Tag().withKey(e.getKey()).withValue(e.getValue()))
            .collect(Collectors.toList());

    System.out.println(ec2Tags);

    RunInstancesRequest request = new RunInstancesRequest()
            .withImageId(imageId)
            .withInstanceType(instanceType)
            .withMinCount(1)
            .withMaxCount(1)
            .withSubnetId(subnetId)
            .withIamInstanceProfile(new IamInstanceProfileSpecification().withArn(instanceProfileArn))
            .withDisableApiTermination(true)
            .withMonitoring(true)
            .withBlockDeviceMappings(
                    new BlockDeviceMapping()
                            .withDeviceName(rootDiskDeviceName)
                            .withEbs(
                                    new EbsBlockDevice()
                                            .withVolumeSize(rootVolumeSize)
                                            .withDeleteOnTermination(true)
                                            .withVolumeType(VolumeType.Gp2)
                            )
            )
            .withTagSpecifications(new TagSpecification().withResourceType(ResourceType.Instance).withTags(ec2Tags))
            .withSecurityGroupIds(securityGroupsIds)
            .withKeyName(keyName);

    System.out.println(request);

    RunInstancesResult result = Ec2Helper.getClient().runInstances(request);

    // Add tags to volumes
    String instanceId = result.getReservation().getInstances().get(0).getInstanceId();
    addTagsToAllVolumes(instanceId);

    System.out.println(result);
  }
}
