package com.github.goober.sonarqube.plugin.decorator.sonarqube;

import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Issue;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Measure;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.sonar.api.ce.posttask.Analysis;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.ce.posttask.QualityGate;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;

@Value
@Builder
public class PullRequestReport {
    @NonNull
    @Getter(AccessLevel.NONE)
    PostProjectAnalysisTask.ProjectAnalysis analysis;

    String pullRequestId;

    String url;

    Set<QualityCondition> qualityConditions;
    Map<String, Measure> measures;

    Set<Issue> issues;

    public Instant getCreationTimestamp() {
        return analysis.getAnalysis()
                .map(Analysis::getDate)
                .map(Date::toInstant)
                .orElse(Instant.now());
    }

    public String getRequiredProperty(String key) {
        return Optional.ofNullable(analysis.getScannerContext().getProperties().get(key))
                .orElseThrow(() -> new IllegalArgumentException(format("Missing required property %s", key)));
    }

    public Optional<String> getRevision() {
        return analysis.getAnalysis()
                .flatMap(Analysis::getRevision);
    }

    public boolean hasMeasures() {
        return !measures.isEmpty();
    }

    public Optional<Measure> getMeasure(String key) {
        return Optional.ofNullable(measures.get(key));
    }

    public QualityGate.Status getStatus() {
        return analysis.getQualityGate().getStatus();
    }
}
