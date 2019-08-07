package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import com.fasterxml.jackson.annotation.JacksonInject;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Value
@RequiredArgsConstructor
public class MeasureResponse {
    Component component;
    @JacksonInject("dashboardUrl")
    String dashboardUrl;

    public Map<String, Measure> getMeasures() {
        return component.getMeasures().stream().collect(toMap(Measure::getMetric, m -> m));
    }

    public boolean hasMeasurements() {
        return !component.getMeasures().isEmpty();
    }

}
