package com.devops.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

public class Volumes {
  public void addTagsToVolumesByInstanceTagName(String instanceTagName, Tag[] tags) {
    AmazonEC2 ec2Client = Ec2Helper.getClient();
    Reservation result = ec2Client.describeInstances(
            new DescribeInstancesRequest()
                    .withFilters(new Filter().withName("tag:Name").withValues(instanceTagName))
    ).getReservations().get(0);

    List<InstanceBlockDeviceMapping> mappings = result
            .getInstances()
            .get(0)
            .getBlockDeviceMappings();
    List<String> volumeIds = mappings
            .stream()
            .map(mapping -> mapping.getEbs().getVolumeId())
            .collect(Collectors.toList());

    ec2Client.createTags(
            new CreateTagsRequest()
                    .withResources(volumeIds)
                    .withTags(tags)
    );
  }

  public void addTagsToVolumeById(String volumeId, Tag[] tags) {
    Ec2Helper.getClient().createTags(
            new CreateTagsRequest()
                    .withResources(volumeId)
                    .withTags(tags)
    );
  }
}