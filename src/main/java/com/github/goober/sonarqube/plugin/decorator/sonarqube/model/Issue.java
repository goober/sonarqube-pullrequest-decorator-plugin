package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import com.fasterxml.jackson.annotation.JacksonInject;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Builder
@Value
public class Issue {
    String key;
    String rule;
    String severity;
    Integer line;
    String component;
    String message;
    String type;
    String project;
    String pullRequest;

    @JacksonInject("baseUrl")
    @Getter(AccessLevel.NONE)
    String baseUrl;

    public String getIssueUrl() {
        return String.format("%s/project/issues?id=%s&pullRequest=%s&open=%s", baseUrl, project, pullRequest, key);
    }

    public String getPath() {
        return component.substring(component.lastIndexOf(':') + 1);
    }
}
