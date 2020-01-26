package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReportPluginTests {
    @Test
    void reportPluginWithGroupIdArtifactIdOnly() {
        MavenReportPlugin reportPlugin = new MavenReportPlugin.Builder("com.example", "demo")
                .build();

        assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
        assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
        assertThat(reportPlugin.getConfiguration()).isNull();
        assertThat(reportPlugin.getInherited()).isNull();
        assertThat(reportPlugin.getVersion()).isNull();
        assertThat(reportPlugin.getReportSets()).isNull();
    }

    @Test
    void reportPluginWithFullData() {
        MavenReportPlugin reportPlugin = new MavenReportPlugin
                .Builder("com.example", "demo")
                .configuration(conf -> conf
                        .property("property1", "value1")
                        .property("property2", "value2"))
                .inherited("inherited1")
                .version("version1")
                .reportSets(reportSets -> reportSets
                        .add("reportSet1")
                        .add("reportSet2"))
                .build();

        assertThat(reportPlugin.getGroupId()).isEqualTo("com.example");
        assertThat(reportPlugin.getArtifactId()).isEqualTo("demo");
        assertThat(reportPlugin.getConfiguration().has("property1")).isTrue();
        assertThat(reportPlugin.getConfiguration().has("property2")).isTrue();
        assertThat(reportPlugin.getInherited()).isEqualTo("inherited1");
        assertThat(reportPlugin.getVersion()).isEqualTo("version1");
        assertThat(reportPlugin.getReportSets().has("reportSet1")).isTrue();
        assertThat(reportPlugin.getReportSets().has("reportSet2")).isTrue();
    }
}