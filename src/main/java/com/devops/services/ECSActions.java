package com.devops.services;

import com.devops.aws.ecs.ECSCluster;

import java.util.List;

public class ECSActions {
    public void forceRestartServices(List<String> clusterArns) {
         clusterArns.forEach(clusterArn -> {
            new ECSCluster(clusterArn).restartAllServices(null, ".*socket.*");
        });
    }
}
