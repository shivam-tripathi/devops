package com.devops.aws.ecs;

import com.amazonaws.services.ecs.model.ListTasksRequest;
import com.amazonaws.services.ecs.model.ListTasksResult;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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

    public List<String> listAllTaskArns() {
        String token = null;
        List<String> res = new ArrayList<>();
        do {
            ListTasksResult iterRes = ECSHelper.getClient().listTasks(
                new ListTasksRequest().withNextToken(token).withServiceName(arn).withCluster(clusterArn)
            );
            token = iterRes.getNextToken();
            res.addAll(iterRes.getTaskArns());
        } while (token != null);

        return res;
    }

    @Override
    public String toString() {
        return "ECSService{" +
                "serviceArn='" + arn + '\'' +
                ", clusterArn='" + clusterArn + '\'' +
                '}';
    }
}

