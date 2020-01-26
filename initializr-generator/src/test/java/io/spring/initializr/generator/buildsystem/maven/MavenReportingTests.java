package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReportingTests {
    @Test
    void reportingEmpty() {
        MavenReporting reporting = new MavenReporting.Builder()
                .build();
        assertThat(reporting.getOutputDirectory()).isNull();
        assertThat(reporting.isExcludeDefaults()).isNull();
        assertThat(reporting.getReportPlugins()).isNull();
    }

    @Test
    void reportingWithFullData() {
        MavenReporting reporting = new MavenReporting
                .Builder()
                .excludeDefaults(true)
                .outputDirectory("output")
                .reportPlugins(reportPlugins -> reportPlugins
                        .add("com.example", "demo")
                        .add("com.example", "demo2"))
                .build();

        assertThat(reporting.isExcludeDefaults()).isTrue();
        assertThat(reporting.getOutputDirectory()).isEqualTo("output");
        assertThat(reporting.getReportPlugins().has("com.example", "demo")).isTrue();
        assertThat(reporting.getReportPlugins().has("com.example", "demo2")).isTrue();
    }
}