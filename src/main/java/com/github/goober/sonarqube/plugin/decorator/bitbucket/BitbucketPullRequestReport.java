package com.github.goober.sonarqube.plugin.decorator.bitbucket;

import com.github.goober.sonarqube.plugin.decorator.PullRequestProperties;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.Annotation;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.CreateReportRequest;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.DataValue;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.ReportData;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Issue;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Measure;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.MeasureResponse;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Metric;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Rating;
import lombok.Getter;
import org.sonar.api.ce.posttask.Analysis;
import org.sonar.api.ce.posttask.Branch;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.QualityGate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@Getter
class BitbucketPullRequestReport {
    private static final String MISSING_PROPERTY = "Missing required property %s";

    private final PostProjectAnalysisTask.ProjectAnalysis analysis;
    private final String pullRequestId;
    private final String project;
    private final String repository;
    private final String revision;

    private String details;
    private List<ReportData> data;
    private Set<Annotation> annotations;

    BitbucketPullRequestReport(PostProjectAnalysisTask.ProjectAnalysis analysis) {
        this.analysis = Optional.ofNullable(analysis)
                .orElseThrow(() -> new IllegalArgumentException("analysis cannot be empty"));

        this.pullRequestId = analysis.getBranch().flatMap(Branch::getName)
                .orElseThrow(() -> new IllegalArgumentException(format(MISSING_PROPERTY, PullRequestProperties.KEY.getKey())));

        this.project = Optional.ofNullable(analysis.getScannerContext().getProperties().get(BitbucketProperties.PROJECT.getKey()))
                .orElseThrow(() -> new IllegalArgumentException(format(MISSING_PROPERTY, BitbucketProperties.PROJECT.getKey())));

        this.repository = Optional.ofNullable(analysis.getScannerContext().getProperties().get(BitbucketProperties.REPOSITORY.getKey()))
                .orElseThrow(() -> new IllegalArgumentException(format(MISSING_PROPERTY, BitbucketProperties.REPOSITORY)));

        this.revision = analysis.getAnalysis()
                .flatMap(Analysis::getRevision)
                .orElseThrow(() -> new IllegalArgumentException("Missing revision information"));
    }

    String getResult() {
        return Optional.ofNullable(analysis.getQualityGate())
                .map(QualityGate::getStatus)
                .map(this::asInsightStatus)
                .orElse("PASS");
    }

    Instant getCreatedDate() {
        return analysis.getAnalysis()
                .map(Analysis::getDate)
                .map(Date::toInstant)
                .orElse(Instant.now());
    }

    BitbucketPullRequestReport setReportDetails(Set<Metric> availableMetrics) {
        String header = analysis.getQualityGate().getStatus().equals(QualityGate.Status.ERROR) ? "Quality Gate failed" : "Quality Gate passed";
        String body = "";
        if (analysis.getQualityGate().getStatus().equals(QualityGate.Status.ERROR)) {
            Map<String, Metric> metrics = availableMetrics.stream()
                    .collect(toMap(Metric::getKey, m -> m));

            body = analysis.getQualityGate().getConditions().stream()
                    .filter(c -> c.getStatus().equals(QualityGate.EvaluationStatus.ERROR))
                    .map(c -> format("- %s %n", toString(c, metrics.get(c.getMetricKey()))))
                    .collect(Collectors.joining(""));
        }
        this.details = format("%s%n%s", header, body);
        return this;
    }

    BitbucketPullRequestReport setReportData(MeasureResponse response) {
        List<ReportData> reportData = new ArrayList<>();
        if (response.hasMeasurements()) {
            Map<String, Measure> measures = response.getMeasures();
            reportData.addAll(Arrays.asList(
                    ReportData.builder()
                            .title("Bugs")
                            .value(new DataValue.Text(Optional.ofNullable(measures.get("new_bugs"))
                                    .flatMap(Measure::firstValue)
                                    .orElse("-")))
                            .build(),
                    ReportData.builder()
                            .title("Code Coverage")
                            .value(Optional.ofNullable(measures.get("new_coverage"))
                                    .flatMap(Measure::firstValue)
                                    .map(BigDecimal::new)
                                    .map(DataValue.Percentage::new)
                                    .map(DataValue.class::cast)
                                    .orElseGet(() -> new DataValue.Text("-"))
                            )
                            .build(),
                    ReportData.builder()
                            .title("Vulnerabilities")
                            .value(new DataValue.Text(Optional.ofNullable(measures.get("new_vulnerabilities"))
                                    .flatMap(Measure::firstValue)
                                    .orElse("-")))
                            .build(),
                    ReportData.builder()
                            .title("Duplication")
                            .value(Optional.ofNullable(measures.get("new_duplicated_lines_density"))
                                    .flatMap(Measure::firstValue)
                                    .map(BigDecimal::new)
                                    .map(DataValue.Percentage::new)
                                    .map(DataValue.class::cast)
                                    .orElseGet(() -> new DataValue.Text("-")))
                            .build(),
                    ReportData.builder()
                            .title("Code Smells")
                            .value(new DataValue.Text(Optional.ofNullable(measures.get("new_code_smells"))
                                    .flatMap(Measure::firstValue)
                                    .orElse("-")))
                            .build()
            ));
        }
        reportData.add(ReportData.builder()
                .title("Details")
                .value(DataValue.Link.builder()
                        .linktext("Go to SonarQube")
                        .href(response.getDashboardUrl())
                        .build())
                .build());
        this.data = reportData;
        return this;
    }

    CreateReportRequest toCreateReportRequest() {
        return CreateReportRequest.builder()
                .title("SonarQube")
                .vendor("SonarQube")
                .logoUrl("https://www.sonarqube.org/favicon-152.png")
                .details(getDetails())
                .data(getData())
                .result(getResult())
                .createdDate(getCreatedDate())
                .build();
    }

    BitbucketPullRequestReport setAnnotations(Set<Issue> issues) {
        this.annotations = issues.stream()
                .map(this::toAnnotation)
                .collect(Collectors.toSet());
        return this;
    }

    private Annotation toAnnotation(Issue issue) {
        return Annotation.builder()
                .line(issue.getLine())
                .path(issue.getPath())
                .message(issue.getMessage())
                .link(issue.getIssueUrl())
                .externalId(issue.getKey())
                .severity(toBitbucketSeverity(issue))
                .type(toBitbucketType(issue.getType()))
                .build();
    }

    private String toBitbucketSeverity(Issue issue) {
        if (issue.getSeverity() == null) {
            return "LOW";
        }
        switch (issue.getSeverity()) {
            case "BLOCKER":
            case "CRITICAL":
                return "HIGH";
            case "MAJOR":
                return "MEDIUM";
            default:
                return "LOW";
        }
    }

    private String toBitbucketType(String sonarqubeType) {
        switch (sonarqubeType) {
            case "SECURITY_HOTSPOT":
            case "VULNERABILITY":
                return "VULNERABILITY";
            case "CODE_SMELL":
                return "CODE_SMELL";
            case "BUG":
                return "BUG";
            default:
                return "";
        }
    }

    private String toString(QualityGate.Condition condition, Metric metric) {
        if (metric.getType().equals("RATING")) {
            return format("%s %s (%s %s)",
                    Rating.valueOf(Integer.parseInt(condition.getValue())),
                    metric.getName(),
                    condition.getOperator().equals(QualityGate.Operator.GREATER_THAN) ? "is worse than" : "is better than",
                    Rating.valueOf(Integer.parseInt(condition.getErrorThreshold())));
        }
        return format("%s %s (%s %s)",
                condition.getValue(),
                metric.getName(),
                condition.getOperator().equals(QualityGate.Operator.GREATER_THAN) ? "is greater than" : "is less than",
                condition.getErrorThreshold());
    }

    private String asInsightStatus(QualityGate.Status status) {
        return QualityGate.Status.ERROR.equals(status) ? "FAIL" : "PASS";
    }
}
