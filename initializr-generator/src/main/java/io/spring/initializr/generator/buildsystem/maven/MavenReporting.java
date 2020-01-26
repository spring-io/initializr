package io.spring.initializr.generator.buildsystem.maven;

import java.util.function.Consumer;

public class MavenReporting {
    private final boolean excludeDefaults;

    private final String outputDirectory;

    private final MavenReportPluginContainer reportPlugins;

    protected MavenReporting(Builder builder) {
        this.excludeDefaults = builder.excludeDefaults;
        this.outputDirectory = builder.outputDirectory;
        this.reportPlugins = builder.reportPlugins;
    }

    public static class Builder {

        private boolean excludeDefaults;

        private String outputDirectory;

        private MavenReportPluginContainer reportPlugins = new MavenReportPluginContainer();

        protected Builder() {
        }

        public MavenReporting.Builder excludeDefaults(boolean excludeDefaults) {
            this.excludeDefaults = excludeDefaults;
            return this;
        }

        public MavenReporting.Builder outputDirectory(String outputDirectory) {
            this.outputDirectory = outputDirectory;
            return this;
        }

        public MavenReporting.Builder reportPlugins(Consumer<MavenReportPluginContainer> reportPlugins) {
            reportPlugins.accept(this.reportPlugins);
            return this;
        }

        public MavenReporting build() {
            return new MavenReporting(this);
        }
    }
}
