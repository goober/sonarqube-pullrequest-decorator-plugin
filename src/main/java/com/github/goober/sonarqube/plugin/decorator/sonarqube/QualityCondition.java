package com.github.goober.sonarqube.plugin.decorator.sonarqube;

import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.Metric;
import lombok.Value;
import org.sonar.api.ce.posttask.QualityGate;

@Value
public class QualityCondition {
    QualityGate.Condition condition;
    Metric metric;
}
