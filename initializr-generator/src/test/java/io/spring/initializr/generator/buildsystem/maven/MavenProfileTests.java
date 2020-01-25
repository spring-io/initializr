package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenProfileTests {
    @Test
    void basicProfile() {
        MavenProfile profile = profile("profile1").build();
        assertThat(profile.getId()).isEqualTo("profile1");
    }

    private MavenProfile.Builder profile(String id) {
        return new MavenProfile.Builder(id, buildItemResolver);
    }
}