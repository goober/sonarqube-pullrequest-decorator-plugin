package com.github.goober.sonarqube.plugin.decorator.sonarqube.model;

import java.util.Arrays;

public enum Rating {
    E(5),
    D(4),
    C(3),
    B(2),
    A(1);

    private final int index;

    Rating(int index) {
        this.index = index;
    }

    public static Rating valueOf(int index) {
        return Arrays.stream(values()).filter(r -> r.index == index).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown value '%s'", index)));
    }
}
