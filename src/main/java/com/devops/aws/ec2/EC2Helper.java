package com.devops.aws.ec2;

import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;

public class EC2Helper {
    private static AmazonEC2 ec2Client = null;
    public static AmazonEC2 getClient() {
        if (ec2Client == null) {
            ec2Client = AmazonEC2ClientBuilder.standard().build();
        }
        return ec2Client;
    }

    public void addTagsToAttachedVolume(String instanceTagName, Tag[] tags) {
        Reservation result = ec2Client.describeInstances(
            new DescribeInstancesRequest()
                .withFilters(new Filter().withName("tag:Name").withValues(instanceTagName))
        ).getReservations().get(0);

        List<InstanceBlockDeviceMapping> mappings = result.getInstances().get(0).getBlockDeviceMappings();
        List<String> volumeIds = mappings
            .stream()
            .map(mapping -> mapping.getEbs().getVolumeId())
            .collect(Collectors.toList());

        ec2Client.createTags(
            new CreateTagsRequest()
                .withResources(volumeIds)
                .withTags(tags)
        );
        System.out.println();
    }
}
