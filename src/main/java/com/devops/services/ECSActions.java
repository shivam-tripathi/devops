package com.devops.services;

import com.amazonaws.services.ecs.model.DescribeTasksRequest;
import com.amazonaws.services.ecs.model.Task;
import com.devops.aws.ecs.ECSCluster;
import com.devops.aws.ecs.EcsHelper;
import com.devops.aws.ecs.ECSService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ECSActions {
  /**
   * Force restarts all services in the cluster without changing anything else
   *
   * @param clusterArns List of clusterArns
   */
  public void forceRestartServices(List<String> clusterArns) {
    clusterArns.forEach(clusterArn -> {
      new ECSCluster(clusterArn).restartAllServices(null, ".*socket.*");
    });
  }

  /**
   * Validates if all tasks have been started after the timestamp
   *
   * @param timestamp   Timestamp
   * @param clusterArns List of clusterArns
   */
  public void validateTasks(Long timestamp, String[] clusterArns) {
    for (String clusterArn : clusterArns) {
      System.out.println("Cluster: " + clusterArn);
      new ECSCluster(clusterArn).listAllServiceArns().forEach(serviceArn -> {
        if (Pattern.compile(".*socket.*").matcher(serviceArn).matches()) {
          return;
        }
        ECSService service = new ECSService(serviceArn, clusterArn);
        List<String> taskArns = service.listAllTaskArns();
        System.out.println("\tService: " + serviceArn + " :: Tasks " + taskArns.size());
        int index = 0;
        List<Task> tasks = new ArrayList<>();
        while (index < taskArns.size()) {
          List<String> subTaskArns = taskArns.subList(index, Math.min(index + 100, taskArns.size()));
          index += 100;
          tasks.addAll(
                  EcsHelper.getClient()
                          .describeTasks(new DescribeTasksRequest().withTasks(subTaskArns).withCluster(clusterArn))
                          .getTasks()
          );
        }
        System.out.println("\t\t Tasks: " + tasks.size());
        final Map<String, Integer> status = new HashMap<>();
        tasks.forEach(task -> {
          if (Long.compare(timestamp, task.getStartedAt().getTime()) == -1) {
            status.merge("success", 1, Integer::sum);
            System.out.println("\t\t\u001B[32m✔\u001B[0m " + task.getTaskDefinitionArn());
          } else {
            status.merge("fails", 1, Integer::sum);
            System.out.println("\t\t\u001B[31m✖\u001B[0m " + task.getTaskDefinitionArn());
          }
        });
        System.out.println("\t\t" + status);
      });
    }
  }
}
