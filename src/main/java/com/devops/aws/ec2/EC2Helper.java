package com.devops.aws.ec2;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

public class EC2Helper {
    private static AmazonEC2 ec2Client = null;

    public static AmazonEC2 getClient() {
        if (ec2Client == null) {
            ec2Client = AmazonEC2ClientBuilder.standard().build();
        }
        return ec2Client;
    }
}
