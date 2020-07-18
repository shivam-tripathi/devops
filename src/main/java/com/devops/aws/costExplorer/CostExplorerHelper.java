package com.devops.aws.costExplorer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.costexplorer.AWSCostExplorer;
import com.amazonaws.services.costexplorer.AWSCostExplorerClientBuilder;
import com.amazonaws.services.costexplorer.model.DateInterval;
import com.amazonaws.services.costexplorer.model.Dimension;
import com.amazonaws.services.costexplorer.model.DimensionValues;
import com.amazonaws.services.costexplorer.model.Expression;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageRequest;
import com.amazonaws.services.costexplorer.model.GetCostAndUsageResult;
import com.amazonaws.services.costexplorer.model.Granularity;
import com.amazonaws.services.costexplorer.model.GroupDefinition;
import com.devops.utils.DateUtils;

public class CostExplorerHelper {
    private static AWSCostExplorer costExplorerClient = null;

    public AWSCostExplorer getClient() {
        if (costExplorerClient == null) {
            costExplorerClient = AWSCostExplorerClientBuilder.standard().build();
        }
        return costExplorerClient;
    }

    private GetCostAndUsageRequest getCostAndUsageRequestLastWeek() {
        Date now = new Date();
        String startDate = DateUtils.getDateInFormat(
            DateUtils.addDaysToDate(now, -7),
            "yyyy-MM-dd"
        );
        String endDate = DateUtils.getDateInFormat(now, "yyyy-MM-dd");
        return new GetCostAndUsageRequest()
            .withTimePeriod(
                new DateInterval()
                    .withStart(startDate)
                    .withEnd(endDate)
            )
            .withGranularity(Granularity.DAILY)
            .withMetrics("unBlendedCost");
    }

    public String getCostUsageByServicesLastWeek(String groupBy, String key, String ...services) {
        String nextPageToken = null;
        List<GetCostAndUsageResult> results = new ArrayList<>();
        do {
            GetCostAndUsageResult result = getClient().getCostAndUsage(
                getCostAndUsageRequestLastWeek()
                   .withGroupBy(new GroupDefinition().withType("DIMENSION").withKey("SERVICE"))
                   .withNextPageToken(nextPageToken)
            );
            nextPageToken = result.getNextPageToken();
        } while (nextPageToken != null);
        return String.join(",", results.stream().map(result -> result.toString()).collect(Collectors.toList()));
    }

    public String getCostUsageBySpecificServiceLastWeek(String serviceName) {
        String nextPageToken = null;
        List<GetCostAndUsageResult> results = new ArrayList<>();
        Expression expression =  new Expression().withDimensions(
            new DimensionValues()
                .withKey(Dimension.SERVICE)
                .withValues(serviceName)
        );
        do {
            GetCostAndUsageResult result = getClient().getCostAndUsage(
                getCostAndUsageRequestLastWeek()
                    .withFilter(expression)
                    .withNextPageToken(nextPageToken)
            );
            nextPageToken = result.getNextPageToken();
        } while (nextPageToken != null);
        return String.join(",", results.stream().map(result -> result.toString()).collect(Collectors.toList()));
    }
}
