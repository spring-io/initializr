package io.spring.initializr.generator.buildsystem.maven;

import java.util.function.Consumer;

public class MavenReporting {
    private final Boolean excludeDefaults;

    private final String outputDirectory;

    private final MavenReportPluginContainer reportPlugins;

    protected MavenReporting(Builder builder) {
        this.excludeDefaults = builder.excludeDefaults;
        this.outputDirectory = builder.outputDirectory;
        this.reportPlugins = builder.reportPlugins;
    }

    public Boolean isExcludeDefaults() {
        return excludeDefaults;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public MavenReportPluginContainer getReportPlugins() {
        return reportPlugins;
    }

    public static class Builder {

        private Boolean excludeDefaults;

        private String outputDirectory;

        private MavenReportPluginContainer reportPlugins;

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
            if(this.reportPlugins == null){
                this.reportPlugins = new MavenReportPluginContainer();
            }
            reportPlugins.accept(this.reportPlugins);
            return this;
        }

        public MavenReporting build() {
            return new MavenReporting(this);
        }
    }
}
