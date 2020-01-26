package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenProfileActivationFileTests {
    @Test
    void profileActivationFileEmpty() {
        MavenProfileActivationFile profileActivationFile = new MavenProfileActivationFile.Builder()
                .build();
        assertThat(profileActivationFile.getExists()).isNull();
        assertThat(profileActivationFile.getMissing()).isNull();
    }

    @Test
    void profileActivationFileWithFullData() {
        MavenProfileActivationFile profileActivationFile = new MavenProfileActivationFile
                .Builder()
                .exists("exists1")
                .missing("missing1")
                .build();

        assertThat(profileActivationFile.getExists()).isEqualTo("exists1");
        assertThat(profileActivationFile.getMissing()).isEqualTo("missing1");
    }
}