package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import static java.lang.String.format;

@Value
@AllArgsConstructor
public class ServerProperties {
    private static final int CODE_INSIGHT_MAJOR_VERSION = 5;
    private static final int CODE_INSIGHT_MINOR_VERSION = 15;

    public static String CODE_INSIGHT_VERSION = format("%d.%d",
            CODE_INSIGHT_MAJOR_VERSION,
            CODE_INSIGHT_MINOR_VERSION);

    String version;

    public boolean hasCodeInsightsApi() {
        String[] semver = semver(version);
        return Integer.parseInt(semver[0]) >= CODE_INSIGHT_MAJOR_VERSION &&
                Integer.parseInt(semver[1]) >= CODE_INSIGHT_MINOR_VERSION;
    }

    private String[] semver(String v) {
        return v.split("\\.");
    }
}
