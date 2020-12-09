package com.devops.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ec2Volumes {
  public void addTagsToVolumesByInstanceTagName(String instanceTagName, List<Tag> tags) {
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

    volumeIds.forEach(volumeId -> addTagsToVolumeById(volumeId, tags));
  }

  public void addTagsToVolumeById(String volumeId, List<Tag> tags) {
    Ec2Helper.getClient().createTags(
            new CreateTagsRequest()
                    .withResources(volumeId)
                    .withTags(tags)
    );
  }

  public void createVolume(int size, String availabilityZone, Map<String, String> tags) {
    CreateVolumeRequest request = new CreateVolumeRequest()
            .withSize(size)
            .withAvailabilityZone(availabilityZone)
            .withTagSpecifications(
                    new TagSpecification().withTags(
                            tags.entrySet()
                                    .stream()
                                    .map(tag -> new Tag().withKey(tag.getKey()).withValue(tag.getValue()))
                                    .collect(Collectors.toList())
                    )
            );
    CreateVolumeResult result = Ec2Helper.getClient().createVolume(request);
  }
}