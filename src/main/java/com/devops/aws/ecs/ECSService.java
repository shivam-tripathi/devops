package com.devops.aws.ecs;

import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ECSService {
    private final String arn;
    private final String clusterArn;

    public void forceNewDeployment() {
        ECSHelper.getClient().updateService(
                new UpdateServiceRequest().withService(arn).withCluster(clusterArn).withForceNewDeployment(true)
        );
        System.out.println("Force new deployment for service: " + this);
    }

    @Override
    public String toString() {
        return "ECSService{" +
                "serviceArn='" + arn + '\'' +
                ", clusterArn='" + clusterArn + '\'' +
                '}';
    }
}

