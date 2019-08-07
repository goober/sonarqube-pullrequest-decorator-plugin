package com.github.goober.sonarqube.plugin.decorator;

public enum PullRequestProperties {
    BRANCH("sonar.pullrequest.branch"),
    BASE("sonar.pullrequest.base"),
    KEY("sonar.pullrequest.key");

    private String key;

    PullRequestProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
