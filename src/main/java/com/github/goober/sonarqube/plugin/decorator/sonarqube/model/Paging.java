package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Paging {

    Integer pageIndex;
    Integer pageSize;
    Integer total;
}