package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenProfileActivationTests {
    @Test
    void profileActivationEmpty() {
        MavenProfileActivation profileActivation = new MavenProfileActivation.Builder()
                .build();
        assertThat(profileActivation.getActiveByDefault()).isNull();
        assertThat(profileActivation.getJdk()).isNull();
        assertThat(profileActivation.getOs()).isNull();
        assertThat(profileActivation.getProperty()).isNull();
        assertThat(profileActivation.getFile()).isNull();
    }

    @Test
    void profileActivationWithFullData() {
        MavenProfileActivation profileActivation = new MavenProfileActivation
                .Builder()
                .jdk("jdk1")
                .activeByDefault(true)
                .os(os -> os
                        .name("name1"))
                .file(file -> file
                        .exists("yes"))
                .property(property -> property
                        .name("name1"))
                .build();

        assertThat(profileActivation.getActiveByDefault()).isTrue();
        assertThat(profileActivation.getJdk()).isEqualTo("jdk1");
        assertThat(profileActivation.getOs()).isNotNull();
        assertThat(profileActivation.getOs().getName()).isEqualTo("name1");
        assertThat(profileActivation.getProperty()).isNotNull();
        assertThat(profileActivation.getProperty().getName()).isEqualTo("name1");
        assertThat(profileActivation.getFile()).isNotNull();
        assertThat(profileActivation.getFile().getExists()).isEqualTo("yes");
    }
}