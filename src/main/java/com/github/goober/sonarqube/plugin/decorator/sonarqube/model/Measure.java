package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

@Value
@Builder
public class Measure {
    String metric;
    Set<Period> periods;

    public Optional<String> firstValue() {
        return periods.stream().findFirst().map(Period::getValue);
    }

    @Value
    static class Period {
        String value;
    }
}
