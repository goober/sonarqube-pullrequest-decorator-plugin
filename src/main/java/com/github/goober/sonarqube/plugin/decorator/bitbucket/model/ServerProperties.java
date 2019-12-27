package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ServerProperties {
    public static final String CODE_INSIGHT_VERSION = "5.15";

    String version;

    public boolean hasCodeInsightsApi() {
        return compareTo(CODE_INSIGHT_VERSION) >= 0;
    }

    private int compareTo(String other) {
        String[] current = semver(version);
        String[] minimum = semver(other);

        int length = Math.max(current.length, minimum.length);
        for(int i = 0; i < length; i++) {
            int thisPart = i < current.length ?
                    Integer.parseInt(current[i]) : 0;
            int thatPart = i < minimum.length ?
                    Integer.parseInt(minimum[i]) : 0;
            if(thisPart < thatPart)
                return -1;
            if(thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    private String[] semver(String v) {
        return v.split("\\.");
    }
}
