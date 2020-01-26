package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenProfileActivationOSTests {
    @Test
    void profileActivationOSEmpty() {
        MavenProfileActivationOS profileActivationOS = new MavenProfileActivationOS.Builder()
                .build();
        assertThat(profileActivationOS.getName()).isNull();
        assertThat(profileActivationOS.getFamily()).isNull();
        assertThat(profileActivationOS.getArch()).isNull();
        assertThat(profileActivationOS.getVersion()).isNull();
    }

    @Test
    void profileActivationOSWithFullData() {
        MavenProfileActivationOS profileActivationOS = new MavenProfileActivationOS
                .Builder()
                .name("name1")
                .family("family1")
                .arch("arch1")
                .version("version1")
                .build();

        assertThat(profileActivationOS.getName()).isEqualTo("name1");
        assertThat(profileActivationOS.getFamily()).isEqualTo("family1");
        assertThat(profileActivationOS.getArch()).isEqualTo("arch1");
        assertThat(profileActivationOS.getVersion()).isEqualTo("version1");
    }
}