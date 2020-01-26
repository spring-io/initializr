package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.PropertyContainer;

import java.util.function.Consumer;

public class MavenReportPlugin {
    private final String groupId;

    private final String artifactId;

    private final String version;

    private final String inherited;

    private final PropertyContainer configuration;

    private final MavenReportSetContainer reportSets;

    protected MavenReportPlugin(Builder builder) {
        this.groupId = builder.groupId;
        this.artifactId = builder.artifactId;
        this.version = builder.version;
        this.inherited = builder.inherited;
        this.configuration = builder.configuration;
        this.reportSets = builder.reportSets;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String getInherited() {
        return inherited;
    }

    public PropertyContainer getConfiguration() {
        return configuration;
    }

    public MavenReportSetContainer getReportSets() {
        return reportSets;
    }

    public static class Builder {

        private final String groupId;

        private final String artifactId;

        private String version;

        private String inherited;

        private PropertyContainer configuration;

        private MavenReportSetContainer reportSets;

        protected Builder(String groupId, String artifactId) {
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public MavenReportPlugin.Builder inherited(String inherited) {
            this.inherited = inherited;
            return this;
        }

        public MavenReportPlugin.Builder configuration(Consumer<PropertyContainer> configuration) {
            configuration.accept(this.configuration);
            return this;
        }

        public MavenReportPlugin.Builder reportSets(Consumer<MavenReportSetContainer> reportSets) {
            reportSets.accept(this.reportSets);
            return this;
        }

        public MavenReportPlugin build() {
            return new MavenReportPlugin(this);
        }
    }
}
