package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ReportData {
    String title;
    DataValue value;
    String type;

    public static class ReportDataBuilder {
        String type;
        DataValue value;

        public ReportDataBuilder value(DataValue value) {
            this.value = value;
            if(value instanceof DataValue.Link) {
                this.type = "LINK";
            } else if(value instanceof DataValue.Percentage) {
                this.type = "PERCENTAGE";
            } else {
                this.type = "TEXT";
            }
            return this;
        }
    }
}
