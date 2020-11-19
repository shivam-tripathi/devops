package com.devops.aws.ecs;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.ListClustersResult;
import com.amazonaws.services.ecs.model.ListServicesRequest;
import com.amazonaws.services.ecs.model.ListServicesResult;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ECSCluster {
    private final String arn;
    public List<String> listAllServiceArns() {
        List<String> res = new ArrayList<>();
        String token = null;
        do {
            ListServicesResult iterRes = ECSHelper.getClient().listServices(
                    new ListServicesRequest().withCluster(arn).withNextToken(token)
            );
            res.addAll(iterRes.getServiceArns());
            token = iterRes.getNextToken();
        } while (token != null);
        return res;
    }

    public List<ECSService> listAllECSServices() {
        return listAllServiceArns()
                .stream()
                .map(serviceArn -> new ECSService(serviceArn, arn))
                .collect(Collectors.toList());
    }

    public void restartAllServices() {
        listAllServiceArns().stream().forEach(serviceArn -> new ECSService(serviceArn, arn).forceNewDeployment());
    }

    public void restartAllServices(String matchingRegex, String notMatchingRegex) {
        Pattern matching = matchingRegex != null ? Pattern.compile(matchingRegex) : null;
        Pattern notMatching = notMatchingRegex != null ? Pattern.compile(notMatchingRegex) : null;
        listAllServiceArns()
                .stream()
                .forEach(serviceArn -> {
                    boolean shouldMatchHolds = matching == null || matching.matcher(serviceArn).matches();
                    boolean shouldNotMatchHolds = notMatching == null || !notMatching.matcher(serviceArn).matches();
                    if (shouldMatchHolds && shouldNotMatchHolds) {
                        new ECSService(serviceArn, this.arn).forceNewDeployment();
                    }
                });
    }
}
