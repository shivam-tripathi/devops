package com.devops.aws.ecs;

import com.amazonaws.services.ecs.model.ListTasksRequest;
import com.amazonaws.services.ecs.model.ListTasksResult;
import com.amazonaws.services.ecs.model.UpdateServiceRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;


@AllArgsConstructor
@Getter
@ToString
public class EcsService {
  private final String arn;
  private final String clusterArn;

  public void forceNewDeployment() {
    EcsHelper.getClient().updateService(
            new UpdateServiceRequest().withService(arn).withCluster(clusterArn).withForceNewDeployment(true)
    );
    System.out.println("Force new deployment for service: " + this);
  }

  public List<String> listAllTaskArns() {
    List<String> res = new ArrayList<>();
    ListTasksRequest request = new ListTasksRequest().withServiceName(arn).withCluster(clusterArn);
    String token = null;
    do {
      ListTasksResult iterRes = EcsHelper.getClient().listTasks(request);
      token = iterRes.getNextToken();
      request.setNextToken(token);
      res.addAll(iterRes.getTaskArns());
    } while (token != null);

    return res;
  }
}

