package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class CreateAnnotationsRequest {
    Set<Annotation> annotations;
}
