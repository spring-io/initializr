package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReportSetTests {
    @Test
    void reportSetWithIdOnly() {
        MavenReportSet reportSet = new MavenReportSet.Builder("id")
                .build();
        assertThat(reportSet.getId()).isEqualTo("id");
        assertThat(reportSet.getConfiguration()).isNull();
        assertThat(reportSet.getInherited()).isNull();
        assertThat(reportSet.getReports()).isNull();
    }

    @Test
    void reportSetWithFullData() {
        MavenReportSet reportSet = new MavenReportSet
                .Builder("id")
                .configuration(conf -> conf
                        .property("property1", "value1")
                        .property("property2", "value2"))
                .inherited("inherited1")
                .report("report1")
                .report("report2")
                .build();

        assertThat(reportSet.getId()).isEqualTo("id");
        assertThat(reportSet.getConfiguration().has("property1")).isTrue();
        assertThat(reportSet.getConfiguration().has("property2")).isTrue();
        assertThat(reportSet.getInherited()).isEqualTo("inherited1");
        assertThat(reportSet.getReports()).isEqualTo(Arrays.asList("report1", "report2"));
    }
}