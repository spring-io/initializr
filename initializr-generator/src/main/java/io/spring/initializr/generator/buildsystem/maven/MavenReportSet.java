package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.PropertyContainer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MavenReportSet {
    private final String id;

    private final PropertyContainer configuration;

    private final String inherited;

    private final List<String> reports;

    public MavenReportSet(Builder builder) {
        this.id = builder.id;
        this.configuration = builder.configuration;
        this.inherited = builder.inherited;
        this.reports = Collections.unmodifiableList(builder.reports);
    }

    public String getId() {
        return id;
    }

    public PropertyContainer getConfiguration() {
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

        private PropertyContainer configuration = new PropertyContainer();

        private String inherited;

        private List<String> reports = new LinkedList<>();

        protected Builder(String id) {
            this.id = id;
        }

        public MavenReportSet.Builder configuration(Consumer<PropertyContainer> configuration) {
            configuration.accept(this.configuration);
            return this;
        }

        public MavenReportSet.Builder inherited(String inherited) {
            this.inherited = inherited;
            return this;
        }

        public MavenReportSet.Builder report(String report) {
            this.reports.add(report);
            return this;
        }

        public MavenReportSet build() {
            return new MavenReportSet(this);
        }
    }
}
