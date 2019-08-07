package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@RequiredArgsConstructor
public class MetricsResponse {
    Set<Metric> metrics;
}
