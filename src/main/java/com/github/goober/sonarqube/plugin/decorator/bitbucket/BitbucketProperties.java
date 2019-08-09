package com.github.goober.sonarqube.plugin.decorator.bitbucket;

import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.Arrays;
import java.util.Collection;

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

    public static Collection<PropertyDefinition> definitions() {
        return Arrays.asList(PropertyDefinition.builder(ENDPOINT.getKey())
                        .index(0)
                        .name("The URL of the Bitbucket Server")
                        .description("This is the base URL for your Bitbucket Server instance")
                        .onQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build(),
                PropertyDefinition.builder(TOKEN.getKey())
                        .index(1)
                        .name("Personal access token")
                        .description("The personal access token of the user that will be used to decorate the pull requests")
                        .onQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build(),
                PropertyDefinition.builder(PROJECT.getKey())
                        .index(2)
                        .name("Bitbucket Server project key")
                        .description("You can find it in the Bitbucket Server repository URL")
                        .onlyOnQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build(),
                PropertyDefinition.builder(REPOSITORY.getKey())
                        .index(3)
                        .name("Bitbucket Server repository slug")
                        .description("You can find it in the Bitbucket Server repository URL")
                        .onlyOnQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build());
    }
}
