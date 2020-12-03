package com.devops.aws.ecs;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.ListClustersRequest;
import com.amazonaws.services.ecs.model.ListClustersResult;
import com.amazonaws.services.ecs.model.ListServicesRequest;
import com.amazonaws.services.ecs.model.ListServicesResult;
import java.util.ArrayList;
import java.util.List;

public class EcsHelper {
  private static AmazonECS ecsClient = null;

  public static AmazonECS getClient() {
    if (ecsClient == null) {
      ecsClient = AmazonECSClientBuilder.standard().build();
    }
    return ecsClient;
  }

  public static List<String> listAllClusterArns() {
    AmazonECS ecsClient = EcsHelper.getClient();
    String token = null;
    List<String> res = new ArrayList<>();
    ListClustersRequest request = new ListClustersRequest().withNextToken(token);
    do {
      ListClustersResult iterRes = ecsClient.listClusters(request);
      token = iterRes.getNextToken();
      request.setNextToken(token);
      res.addAll(iterRes.getClusterArns());
    } while (token != null);
    return res;
  }

  public static List<String> listAllServicesArns() {
    AmazonECS ecsClient = EcsHelper.getClient();
    String token = null;
    List<String> res = new ArrayList<>();
    ListServicesRequest request = new ListServicesRequest();
    do {
      ListServicesResult iterRes = ecsClient.listServices(request);
      token = iterRes.getNextToken();
      request.setNextToken(token);
      res.addAll(iterRes.getServiceArns());
    } while (token != null);
    return res;
  }
}
