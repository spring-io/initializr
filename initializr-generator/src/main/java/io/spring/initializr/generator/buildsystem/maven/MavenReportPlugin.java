package io.spring.initializr.generator.buildsystem.maven;

import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class MavenReportPlugin {
    private final String groupId;

    private final String artifactId;

    private final String version;

    private final String inherited;

    private final MavenConfiguration configuration;

    private final MavenReportSetContainer reportSets;

    protected MavenReportPlugin(Builder builder) {
        this.groupId = builder.groupId;
        this.artifactId = builder.artifactId;
        this.version = builder.version;
        this.inherited = builder.inherited;
        this.configuration = ofNullable(builder.configuration)
                .map(MavenConfiguration.Builder::build)
                .orElse(null);
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

    public MavenConfiguration getConfiguration() {
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

        private MavenConfiguration.Builder configuration;

        private MavenReportSetContainer reportSets;

        protected Builder(String groupId, String artifactId) {
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public MavenReportPlugin.Builder inherited(String inherited) {
            this.inherited = inherited;
            return this;
        }

        public MavenReportPlugin.Builder version(String version) {
            this.version = version;
            return this;
        }

        public MavenReportPlugin.Builder configuration(Consumer<MavenConfiguration.Builder> configuration) {
            if(this.configuration == null){
                this.configuration = new MavenConfiguration.Builder();
            }
            configuration.accept(this.configuration);
            return this;
        }

        public MavenReportPlugin.Builder reportSets(Consumer<MavenReportSetContainer> reportSets) {
            if(this.reportSets == null){
                this.reportSets = new MavenReportSetContainer();
            }
            reportSets.accept(this.reportSets);
            return this;
        }

        public MavenReportPlugin build() {
            return new MavenReportPlugin(this);
        }
    }
}
