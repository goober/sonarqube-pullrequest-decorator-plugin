package com.github.goober.sonarqube.plugin.decorator.bitbucket;

import com.github.goober.sonarqube.plugin.decorator.PullRequestProperties;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

import java.util.Arrays;
import java.util.stream.Stream;

public class BitbucketPropertiesSensor implements Sensor {

    @Override
    public void describe(SensorDescriptor sensorDescriptor) {
        sensorDescriptor.name("Bitbucket Properties Sensor");
    }

    @Override
    public void execute(SensorContext context) {
        Stream.concat(
                Arrays.stream(BitbucketProperties.values())
                        .map(BitbucketProperties::getKey),
                Arrays.stream(PullRequestProperties.values()
                ).map(PullRequestProperties::getKey)
        ).forEach(key -> context.config().get(key)
                .ifPresent(value -> context.addContextProperty(key, value)));
    }
}
