package com.devops.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;


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
    public String name;
    public String type;
    public String role;
    public String env;
    Map<String, String> tags;

    public InstanceDetails(Instance instance) {
      tags = instance.getTags().stream().collect(Collectors.toMap(Tag::getKey, Tag::getValue));
      id = instance.getInstanceId();
      ip = instance.getPublicIpAddress();
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
    String token = null;
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
}
