package com.github.goober.sonarqube.plugin.decorator.sonarqube;

import com.github.goober.sonarqube.plugin.decorator.PullRequestProperties;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.MeasureResponse;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Metric;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.SearchIssuesResponse;
import lombok.RequiredArgsConstructor;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.ce.posttask.Branch;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@ComputeEngineSide
@RequiredArgsConstructor
public class PullRequestReportCollector {

    private static final Logger LOGGER = Loggers.get(PullRequestReportCollector.class);

    private final SonarQubeClient sonarqubeClient;

    public PullRequestReport collect(PostProjectAnalysisTask.ProjectAnalysis analysis) throws IOException {
        String pullRequestId = analysis.getBranch().flatMap(Branch::getName)
                .orElseThrow(() -> new IllegalArgumentException(format("Missing required property %s", PullRequestProperties.KEY.getKey())));

        Map<String, Metric> metrics = sonarqubeClient.listMetrics().getMetrics().stream()
                .collect(toMap(Metric::getKey, m -> m));

        Set<QualityCondition> qualityConditions = analysis.getQualityGate().getConditions().stream()
                .map(condition -> new QualityCondition(condition, metrics.get(condition.getMetricKey())))
                .collect(Collectors.toSet());

        MeasureResponse measures = sonarqubeClient.listMeasures(analysis.getProject().getKey(), pullRequestId,
                "new_code_smells",
                "new_bugs",
                "new_vulnerabilities",
                "new_coverage",
                "new_duplicated_lines_density");

        SearchIssuesResponse issues = sonarqubeClient.listOpenIssues(analysis.getProject().getKey(), pullRequestId);

        return PullRequestReport.builder()
                .pullRequestId(pullRequestId)
                .url(measures.getDashboardUrl())
                .issues(issues.getIssues())
                .qualityConditions(qualityConditions)
                .analysis(analysis)
                .measures(measures.getMeasures())
                .build();
    }
}
