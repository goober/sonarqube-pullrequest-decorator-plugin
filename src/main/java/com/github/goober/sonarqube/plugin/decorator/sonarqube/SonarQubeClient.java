package com.github.goober.sonarqube.plugin.decorator.sonarqube;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.MeasureResponse;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.MetricsResponse;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.model.SearchResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.config.Configuration;
import org.sonar.api.platform.Server;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;
import java.util.Optional;

import static java.lang.String.format;

@ComputeEngineSide
@RequiredArgsConstructor
public class SonarQubeClient {

    private static final Logger LOGGER = Loggers.get(SonarQubeClient.class);

    private final Server server;
    private final Configuration configuration;

    private OkHttpClient client;

    private ObjectMapper objectMapper;

    public SearchResponse listOpenIssues(String project, String pullRequestId) throws IOException {
        Request request = new Request.Builder()
                .url(String.format("%s/api/issues/search?projects=%s&pullRequest=%s", getLocalUrl(), project, pullRequestId))
                .build();

        try (Response response = getClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                return objectMapper().reader(new InjectableValues.Std()
                        .addValue("baseUrl", server.getPublicRootUrl()))
                        .forType(SearchResponse.class)
                        .readValue(response.body().string());
            }
            LOGGER.error("{} - {}", response.code(), response.body() == null ? "" : response.body().string());
            throw new IOException(format("SonarQube's API responded with an unsuccessful response code %d", response.code()));
        }
    }

    public MetricsResponse listMetrics() throws IOException {
        Request request = new Request.Builder()
                .url(String.format("%s/api/metrics/search?ps=500", getLocalUrl()))
                .build();
        try (Response response = getClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                return objectMapper().reader().forType(MetricsResponse.class).readValue(response.body().string());
            }
            LOGGER.error("{} - {}", response.code(), response.body() == null ? "" : response.body().string());
            throw new IOException(format("SonarQube's API responded with an unsuccessful response code %d", response.code()));
        }
    }

    public MeasureResponse listMeasures(String project, String pullRequestId, String... measures) throws IOException {
        Request request = new Request.Builder()
                .url(String.format("%s/api/measures/component?component=%s&pullRequest=%s&metricKeys=%s",
                        getLocalUrl(),
                        project,
                        pullRequestId,
                        String.join(",", measures)))
                .build();
        try (Response response = getClient().newCall(request).execute()) {
            if (response.isSuccessful()) {
                return objectMapper().reader(new InjectableValues.Std()
                        .addValue("dashboardUrl", getDashboardUrl(project, pullRequestId)))
                        .forType(MeasureResponse.class)
                        .readValue(response.body().string());
            }
            LOGGER.error("{} - {}", response.code(), response.body() == null ? "" : response.body().string());
            throw new IOException(format("SonarQube's API responded with an unsuccessful response code %d", response.code()));
        }
    }

    private String getDashboardUrl(String project, String pullRequestId) {
        return String.format("%s/dashboard?id=%s&pullRequest=%s", server.getPublicRootUrl(), project, pullRequestId);
    }

    private String getLocalUrl() {
        return "http://localhost:" + configuration.get("sonar.web.port").orElse("9000") + server.getContextPath();
    }

    private OkHttpClient getClient() {
        client = Optional.ofNullable(client).orElseGet(OkHttpClient::new);
        return client;
    }

    private ObjectMapper objectMapper() {
        objectMapper = Optional.ofNullable(objectMapper)
                .orElseGet(() -> new ObjectMapper()
                        .setSerializationInclusion(Include.NON_NULL)
                        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                );
        return objectMapper;
    }
}
