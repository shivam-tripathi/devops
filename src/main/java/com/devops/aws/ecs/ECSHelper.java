package com.devops.aws.ecs;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.ListClustersRequest;
import com.amazonaws.services.ecs.model.ListClustersResult;
import com.amazonaws.services.ecs.model.ListServicesRequest;
import com.amazonaws.services.ecs.model.ListServicesResult;

import java.util.ArrayList;
import java.util.List;

public class ECSHelper {
    private static AmazonECS ecsClient = null;

    public static AmazonECS getClient() {
        if (ecsClient == null) {
            ecsClient = AmazonECSClientBuilder.standard().build();
        }
        return ecsClient;
    }

    public static List<String> listAllClusterArns() {
        AmazonECS ecsClient = ECSHelper.getClient();
        String nextToken = null;
        List<String> res = new ArrayList<>();
        do {
            ListClustersResult iterRes = ecsClient.listClusters(new ListClustersRequest().withNextToken(nextToken));
            nextToken = iterRes.getNextToken();
            res.addAll(iterRes.getClusterArns());
        } while (nextToken != null);
        return res;
    }

    public static List<String> listAllServicesArns() {
        AmazonECS ecsClient = ECSHelper.getClient();
        String nextToken = null;
        List<String> res = new ArrayList<>();
        do {
            ListServicesResult iterRes = ecsClient.listServices(new ListServicesRequest().withNextToken(nextToken));
            nextToken = iterRes.getNextToken();
            res.addAll(iterRes.getServiceArns());
        } while (nextToken != null);
        return res;
    }
}
