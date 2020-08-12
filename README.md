# Archived
The functionality has been updated and merged into the original plugin and any
issues you find should be opened against [sonarqube-community-branch-plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin)

# Sonarqube Pull Request Decorator Plugin
A plugin for [SonarQube](https://sonarqube.org) to allow pull request decorations in the Community Edition.

**NOTE** This plugin is still a work in progress and should only be used for evaluation at this stage.
There is an [open discussion](https://github.com/mc1arke/sonarqube-community-branch-plugin/issues/27) on whether to add 
support for pull request decorations directly in the `sonarqube-community-branch-plugin`. 
There is a possibility that the features of this plugin get pulled into that repository, depending on how the 
discussion moves forward. 

## Compatibility
The plugin requires SonarQube Community Edition version 7.8 or higher.
[sonarqube-community-branch-plugin](https://github.com/mc1arke/sonarqube-community-branch-plugin) is also a prerequisite
for this plugin to work properly since it enables branch and pull request analysis with the Community Edition.

## Installation
Either build the project or [download a compatible release version of the plugin JAR](https://github.com/goober/sonarqube-pullrequest-decorator-plugin/releases).
Copy the plugin JAR file to the `extensions/plugins/` directory of your SonarQube instance and restart SonarQube.

## Features
The plugin aims to support the
[features and parameters specified in the SonarQube documentation](https://docs.sonarqube.org/latest/analysis/pull-request/).

### Bitbucket Server
The following properties is required to be set to enable the plugin for Bitbucket Server, 
and can be set either in `conf/sonar.properties` or through the user interface under 
Administration > General Settings > Pull Requests > **Integration with Bitbucket Server**

| Property                               | Description                                                             |
| -------------------------------------- | ----------------------------------------------------------------------- |
| `sonar.pullrequest.bitbucket.endpoint` | The server endpoint. e.g `https://bitbucket.company.com`                |
| `sonar.pullrequest.bitbucket.token`    | [The Personal Access Token](https://confluence.atlassian.com/bitbucketserver/personal-access-tokens-939515499.html) to authenticate with the Bitbucket Server API |
