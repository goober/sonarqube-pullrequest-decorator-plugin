package com.github.goober.sonarqube.plugin.decorator;

import org.sonar.api.ce.posttask.Branch;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.utils.log.Loggers;

import java.util.Optional;

public interface PullRequestDecorator extends PostProjectAnalysisTask {

    @Override
    default void finished(ProjectAnalysis analysis) {
        Optional<Branch> pullrequest = analysis.getBranch().filter(b -> Branch.Type.PULL_REQUEST.equals(b.getType()));
        if (!pullrequest.isPresent()) {
            Loggers.get(PullRequestDecorator.class).debug("Analysis of {} is not affecting a pull request, ignoring", analysis.getProject().getKey());
            return;
        }
        if (isActivated()) {
            decorate(analysis);
        } else {
            Loggers.get(PullRequestDecorator.class).info("{} is deactivated. Please read the documentation on how to setup the required properties to activate it",
                    getClass().getCanonicalName());
        }
    }

    default boolean isActivated() {
        return false;
    }

    void decorate(ProjectAnalysis analysis);

}
