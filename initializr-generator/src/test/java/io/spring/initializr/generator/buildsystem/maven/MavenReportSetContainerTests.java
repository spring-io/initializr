package io.spring.initializr.generator.buildsystem.maven;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class MavenReportSetContainerTests {
    @Test
    void addReportSet() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1");
        assertThat(reportSetContainer.values()).hasOnlyOneElementSatisfying((reportSet) -> {
            assertThat(reportSet.getId()).isEqualTo("reportSet1");
            assertThat(reportSet.getInherited()).isNull();
            assertThat(reportSet.getReports()).isNull();
            assertThat(reportSet.getConfiguration()).isNull();
        });
    }

    @Test
    void addReportSetWithConsumer() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1",
                (reportSet) -> reportSet
                        .inherited("inherited1")
                        .report("report1")
                        .report("report2")
                        .configuration(conf -> conf
                                .add("property1", "value1"))
        );

        assertThat(reportSetContainer.values()).hasOnlyOneElementSatisfying((reportSet) -> {
            assertThat(reportSet.getId()).isEqualTo("reportSet1");
            assertThat(reportSet.getConfiguration().getSettings()).hasOnlyOneElementSatisfying(settings -> {
                assertThat(settings.getName()).isEqualTo("property1");
                assertThat(settings.getValue()).isEqualTo("value1");
            });
            assertThat(reportSet.getInherited()).isEqualTo("inherited1");
            assertThat(reportSet.getReports()).isEqualTo(Arrays.asList("report1", "report2"));
        });
    }

    @Test
    void addReportSetSeveralTimeReuseConfiguration() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1", (reportSet) -> reportSet.inherited("inherited1"));
        reportSetContainer.add("reportSet1", (reportSet) -> reportSet.inherited("inherited2"));
        assertThat(reportSetContainer.values()).hasOnlyOneElementSatisfying((reportSet) -> {
            assertThat(reportSet.getId()).isEqualTo("reportSet1");
            assertThat(reportSet.getInherited()).isEqualTo("inherited2");
            assertThat(reportSet.getReports()).isNull();
            assertThat(reportSet.getConfiguration()).isNull();
        });
    }

    @Test
    void isEmptyWithEmptyContainer() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        assertThat(reportSetContainer.isEmpty()).isTrue();
    }

    @Test
    void isEmptyWithRegisteredReportSet() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1");
        assertThat(reportSetContainer.isEmpty()).isFalse();
    }

    @Test
    void hasReportSetWithMatchingReportSet() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1");
        assertThat(reportSetContainer.has("reportSet1")).isTrue();
    }

    @Test
    void hasReportSetWithNonMatchingReportSet() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1");
        assertThat(reportSetContainer.has("reportSet2")).isFalse();
    }

    @Test
    void removeWithMatchingReportSet() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1");
        assertThat(reportSetContainer.remove("reportSet1")).isTrue();
        assertThat(reportSetContainer.isEmpty()).isTrue();
    }

    @Test
    void removeWithNonMatchingReportSet() {
        MavenReportSetContainer reportSetContainer = new MavenReportSetContainer();
        reportSetContainer.add("reportSet1");
        assertThat(reportSetContainer.remove("reportSet2")).isFalse();
        assertThat(reportSetContainer.isEmpty()).isFalse();
    }
}