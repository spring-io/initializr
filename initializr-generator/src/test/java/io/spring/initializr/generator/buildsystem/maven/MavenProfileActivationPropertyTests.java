package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenProfileActivationPropertyTests {
    @Test
    void profileActivationPropertyEmpty() {
        MavenProfileActivationProperty profileActivationProperty = new MavenProfileActivationProperty.Builder()
                .build();
        assertThat(profileActivationProperty.getName()).isNull();
        assertThat(profileActivationProperty.getValue()).isNull();
    }

    @Test
    void profileActivationPropertyWithFullData() {
        MavenProfileActivationProperty profileActivationProperty = new MavenProfileActivationProperty
                .Builder()
                .name("name1")
                .value("value1")
                .build();

        assertThat(profileActivationProperty.getName()).isEqualTo("name1");
        assertThat(profileActivationProperty.getValue()).isEqualTo("value1");
    }
}