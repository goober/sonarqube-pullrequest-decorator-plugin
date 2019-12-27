package com.github.goober.sonarqube.plugin.decorator.bitbucket.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerPropertiesTest {

    @Test
    public void server_with_version_without_code_insights_returns_false() {
       ServerProperties underTest = new ServerProperties("2.1");
       assertFalse(underTest.hasCodeInsightsApi());
    }

    @Test
    public void server_version_greater_than_minimum_for_code_insights_returns_true() {
        ServerProperties underTest = new ServerProperties("6.7.1");
        assertTrue(underTest.hasCodeInsightsApi());
    }

    @Test
    public void server_version_equals_to_minimum_for_code_insights_return_true() {
        ServerProperties underTest = new ServerProperties(ServerProperties.CODE_INSIGHT_VERSION);
        assertTrue(underTest.hasCodeInsightsApi());
    }
}
