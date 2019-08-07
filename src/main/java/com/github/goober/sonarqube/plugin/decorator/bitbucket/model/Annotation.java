package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Annotation {
    String externalId;
    int line;
    String link;
    String message;
    String path;
    String severity;
    String type;
}
