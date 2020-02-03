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
                        .add("property1", "value1"))
                .inherited("inherited1")
                .report("report1")
                .report("report2")
                .build();

        assertThat(reportSet.getId()).isEqualTo("id");
        assertThat(reportSet.getConfiguration().getSettings()).hasOnlyOneElementSatisfying(settings -> {
            assertThat(settings.getName()).isEqualTo("property1");
            assertThat(settings.getValue()).isEqualTo("value1");
        });
        assertThat(reportSet.getInherited()).isEqualTo("inherited1");
        assertThat(reportSet.getReports()).isEqualTo(Arrays.asList("report1", "report2"));
    }
}