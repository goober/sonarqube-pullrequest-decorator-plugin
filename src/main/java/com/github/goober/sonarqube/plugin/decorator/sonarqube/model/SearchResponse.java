package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class SearchResponse {

    Integer total;
    Integer p;
    Integer ps;
    Paging paging;

    Set<Issue> issues;

}

