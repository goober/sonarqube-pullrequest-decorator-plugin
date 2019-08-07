package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@Builder
public class CreateReportRequest {
    List<ReportData> data;
    String details;
    String title;
    String vendor;
    Instant createdDate;
    String link;
    String logoUrl;
    String result;
}