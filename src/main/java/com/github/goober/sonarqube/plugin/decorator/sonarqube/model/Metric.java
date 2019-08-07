package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class Metric {

    String key;
    String name;
    String description;
    String type;
}
