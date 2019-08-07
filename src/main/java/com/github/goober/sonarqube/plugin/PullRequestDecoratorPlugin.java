/*
 * Copyright (C) 2019 Michael Clarke
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.github.goober.sonarqube.plugin;

import com.github.goober.sonarqube.plugin.decorator.bitbucket.BitbucketClient;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.BitbucketProperties;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.BitbucketPropertiesSensor;
import com.github.goober.sonarqube.plugin.decorator.bitbucket.BitbucketPullRequestDecorator;
import com.github.goober.sonarqube.plugin.decorator.sonarqube.SonarQubeClient;
import org.sonar.api.Plugin;
import org.sonar.api.SonarQubeSide;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

public class PullRequestDecoratorPlugin implements Plugin {

    @Override
    public void define(Context context) {
        if (SonarQubeSide.SCANNER == context.getRuntime().getSonarQubeSide()) {
            context.addExtension(BitbucketPropertiesSensor.class);
        }
        if (SonarQubeSide.COMPUTE_ENGINE == context.getRuntime().getSonarQubeSide()) {
            context.addExtensions(SonarQubeClient.class, BitbucketClient.class, BitbucketPullRequestDecorator.class);
        }

        context.addExtensions(
                PropertyDefinition.builder(BitbucketProperties.ENDPOINT.getKey())
                        .index(0)
                        .name("The URL of the Bitbucket Server")
                        .description("This is the base URL for your Bitbucket Server instance")
                        .onQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build(),
                PropertyDefinition.builder(BitbucketProperties.TOKEN.getKey())
                        .index(1)
                        .name("Personal access token")
                        .description("The personal access token of the user that will be used to decorate the pull requests")
                        .onQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build(),
                PropertyDefinition.builder(BitbucketProperties.PROJECT.getKey())
                        .index(2)
                        .name("Bitbucket Server project key")
                        .description("You can find it in the Bitbucket Server repository URL")
                        .onlyOnQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build(),
                PropertyDefinition.builder(BitbucketProperties.REPOSITORY.getKey())
                        .index(3)
                        .name("Bitbucket Server repository slug")
                        .description("You can find it in the Bitbucket Server repository URL")
                        .onlyOnQualifiers(Qualifiers.PROJECT)
                        .category("pullrequest").subCategory("bitbucket")
                        .build());

    }

}
