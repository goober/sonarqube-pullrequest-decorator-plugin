package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@Builder
@RequiredArgsConstructor
public class Component {
    Set<Measure> measures;

}
