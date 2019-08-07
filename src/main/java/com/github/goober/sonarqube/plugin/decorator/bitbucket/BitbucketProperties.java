package com.github.goober.sonarqube.plugin.decorator.bitbucket;

public enum BitbucketProperties {
    ENDPOINT("sonar.pullrequest.bitbucket.endpoint"),
    TOKEN("sonar.pullrequest.bitbucket.token.secured"),

    PROJECT("sonar.pullrequest.bitbucket.project"),
    REPOSITORY("sonar.pullrequest.bitbucket.repository");

    private String key;

    BitbucketProperties(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
