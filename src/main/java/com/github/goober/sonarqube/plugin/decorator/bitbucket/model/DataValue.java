package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.math.BigDecimal;

public abstract class DataValue {

    @Value
    @Builder
    @EqualsAndHashCode(callSuper=false)
    public static class Link extends DataValue {
        String linktext;
        String href;
    }

    @AllArgsConstructor
    @Value
    @EqualsAndHashCode(callSuper=false)
    public static class Text extends DataValue {
        @JsonValue
        String value;
    }

    @AllArgsConstructor
    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Percentage extends DataValue {
        @JsonValue
        BigDecimal value;
    }
}
