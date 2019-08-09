package com.github.goober.sonarqube.plugin.decorator.bitbucket;

import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.Annotation;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.CreateReportRequest;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.DataValue;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.ReportData;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.PullRequestReport;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.QualityCondition;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Issue;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Measure;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Rating;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sonar.api.ce.posttask.QualityGate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@RequiredArgsConstructor
class BitbucketPullRequestReport {

    @NonNull
    private final PullRequestReport report;

    String getProject() {
        return report.getRequiredProperty(BitbucketProperties.PROJECT.getKey());
    }

    String getRepository() {
        return report.getRequiredProperty(BitbucketProperties.REPOSITORY.getKey());
    }

    String getRevision() {
        return report.getRevision()
                .orElseThrow(() -> new IllegalArgumentException("Missing revision information"));
    }

    String getResult() {
        return Optional.ofNullable(report.getStatus())
                .map(this::asInsightStatus)
                .orElse("PASS");
    }

    String getReportDetails() {
        String header = report.getStatus().equals(QualityGate.Status.ERROR) ? "Quality Gate failed" : "Quality Gate passed";
        String body = "";
        if (report.getStatus().equals(QualityGate.Status.ERROR)) {
            body = report.getQualityConditions().stream()
                    .filter(c -> QualityGate.EvaluationStatus.ERROR.equals(c.getCondition().getStatus()))
                    .map(c -> format("- %s %n", toString(c)))
                    .collect(Collectors.joining(""));
        }
        return format("%s%n%s", header, body);
    }

    List<ReportData> getReportData() {
        List<ReportData> reportData = new ArrayList<>();
        if (report.hasMeasures()) {
            reportData.addAll(Arrays.asList(
                    ReportData.builder()
                            .title("Bugs")
                            .value(new DataValue.Text(report.getMeasure("new_bugs")
                                    .flatMap(Measure::firstValue)
                                    .orElse("-")))
                            .build(),
                    ReportData.builder()
                            .title("Code Coverage")
                            .value(report.getMeasure("new_coverage")
                                    .flatMap(Measure::firstValue)
                                    .map(BigDecimal::new)
                                    .map(DataValue.Percentage::new)
                                    .map(DataValue.class::cast)
                                    .orElseGet(() -> new DataValue.Text("-"))
                            )
                            .build(),
                    ReportData.builder()
                            .title("Vulnerabilities")
                            .value(new DataValue.Text(report.getMeasure("new_vulnerabilities")
                                    .flatMap(Measure::firstValue)
                                    .orElse("-")))
                            .build(),
                    ReportData.builder()
                            .title("Duplication")
                            .value(report.getMeasure("new_duplicated_lines_density")
                                    .flatMap(Measure::firstValue)
                                    .map(BigDecimal::new)
                                    .map(DataValue.Percentage::new)
                                    .map(DataValue.class::cast)
                                    .orElseGet(() -> new DataValue.Text("-")))
                            .build(),
                    ReportData.builder()
                            .title("Code Smells")
                            .value(new DataValue.Text(report.getMeasure("new_code_smells")
                                    .flatMap(Measure::firstValue)
                                    .orElse("-")))
                            .build()
            ));
        }
        reportData.add(ReportData.builder()
                .title("Details")
                .value(DataValue.Link.builder()
                        .linktext("Go to SonarQube")
                        .href(report.getUrl())
                        .build())
                .build());
        return reportData;
    }

    CreateReportRequest toCreateReportRequest() {
        return CreateReportRequest.builder()
                .title("SonarQube")
                .vendor("SonarQube")
                .logoUrl("https://www.sonarqube.org/favicon-152.png")
                .details(getReportDetails())
                .data(getReportData())
                .result(getResult())
                .createdDate(report.getCreationTimestamp())
                .build();
    }

    Set<Annotation> getAnnotations() {
        return report.getIssues().stream()
                .map(this::toAnnotation)
                .collect(Collectors.toSet());
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

    private String toString(QualityCondition condition) {
        if (condition.getMetric().getType().equals("RATING")) {
            return format("%s %s (%s %s)",
                    Rating.valueOf(Integer.parseInt(condition.getCondition().getValue())),
                    condition.getMetric().getName(),
                    condition.getCondition().getOperator().equals(QualityGate.Operator.GREATER_THAN) ? "is worse than" : "is better than",
                    Rating.valueOf(Integer.parseInt(condition.getCondition().getErrorThreshold())));
        }
        return format("%s %s (%s %s)",
                condition.getCondition().getValue(),
                condition.getMetric().getName(),
                condition.getCondition().getOperator().equals(QualityGate.Operator.GREATER_THAN) ? "is greater than" : "is less than",
                condition.getCondition().getErrorThreshold());
    }

    private String asInsightStatus(QualityGate.Status status) {
        return QualityGate.Status.ERROR.equals(status) ? "FAIL" : "PASS";
    }
}
