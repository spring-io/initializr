package io.spring.initializr.generator.buildsystem.maven;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class MavenReportSet {
    private final String id;

    private final MavenConfiguration configuration;

    private final String inherited;

    private final List<String> reports;

    public MavenReportSet(Builder builder) {
        this.id = builder.id;
        this.configuration = ofNullable(builder.configuration)
                .map(MavenConfiguration.Builder::build)
                .orElse(null);
        this.inherited = builder.inherited;
        this.reports = ofNullable(builder.reports)
                .map(Collections::unmodifiableList)
                .orElse(null);
    }

    public String getId() {
        return id;
    }

    public MavenConfiguration getConfiguration() {
        return configuration;
    }

    public String getInherited() {
        return inherited;
    }

    public List<String> getReports() {
        return reports;
    }

    public static class Builder {

        private final String id;

        private MavenConfiguration.Builder configuration;

        private String inherited;

        private List<String> reports;

        protected Builder(String id) {
            this.id = id;
        }

        public MavenReportSet.Builder configuration(Consumer<MavenConfiguration.Builder> configuration) {
            if(this.configuration == null){
                this.configuration = new MavenConfiguration.Builder();
            }
            configuration.accept(this.configuration);
            return this;
        }

        public MavenReportSet.Builder inherited(String inherited) {
            this.inherited = inherited;
            return this;
        }

        public MavenReportSet.Builder report(String report) {
            if(this.reports == null){
                this.reports = new LinkedList<>();
            }
            this.reports.add(report);
            return this;
        }

        public MavenReportSet build() {
            return new MavenReportSet(this);
        }
    }
}
