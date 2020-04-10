package com.github.goober.sonarqube.plugin.decorator.bitbucket;

import com.github.goober.sonarqube.plugin.decorator.PullRequestDecorator;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.CreateAnnotationsRequest;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.model.ServerProperties;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.PullRequestReportCollector;
import lombok.RequiredArgsConstructor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;

import static java.lang.String.format;

@RequiredArgsConstructor
public class BitbucketPullRequestDecorator implements PullRequestDecorator {

    private static final Logger LOGGER = Loggers.get(BitbucketPullRequestDecorator.class);

    private final PullRequestReportCollector reportCollector;
    private final BitbucketClient bitbucketClient;

    @Override
    public boolean isActivated() {
        return bitbucketClient.isConfigured() && hasApiSupport();
    }

    @Override
    public void decorate(ProjectAnalysis analysis) {
        try {
            BitbucketPullRequestReport report = new BitbucketPullRequestReport(reportCollector.collect(analysis));

            bitbucketClient.createReport(report.getProject(),
                    report.getRepository(),
                    report.getRevision(),
                    report.toCreateReportRequest()
            );

            bitbucketClient.deleteAnnotations(report.getProject(), report.getRepository(), report.getRevision());

            bitbucketClient.createAnnotations(report.getProject(),
                    report.getRepository(),
                    report.getRevision(),
                    CreateAnnotationsRequest.builder()
                            .annotations(report.getAnnotations())
                            .build());

        } catch (IOException e) {
            LOGGER.error("Could not decorate pull request for project {}", analysis.getProject().getKey(), e);
        }
    }

    private boolean hasApiSupport() {
        try {
            ServerProperties server = bitbucketClient.getServerProperties();
            LOGGER.debug(format("Your Bitbucket Server installation is version %s", server.getVersion()));
            if (server.hasCodeInsightsApi()) {
                return true;
            } else {
                LOGGER.info(format("Bitbucket Server version is to old. %s is the minimum version that supports code insights",
                        ServerProperties.CODE_INSIGHT_VERSION));
            }
        } catch (IOException e) {
            LOGGER.error("Could not determine Bitbucket Server version", e);
            return false;
        }
        return false;
    }

}
