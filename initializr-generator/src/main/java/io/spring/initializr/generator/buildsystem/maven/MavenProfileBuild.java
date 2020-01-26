package io.spring.initializr.generator.buildsystem.maven;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class MavenProfileBuild {
    private final String defaultGoal;

    private final String directory;

    private final String finalName;

    private final List<String> filters;

    private final MavenResourceContainer resources;

    private final MavenResourceContainer testResources;

    private final MavenPluginManagement pluginManagement;

    private final MavenPluginContainer plugins;

    protected MavenProfileBuild(Builder builder) {
        this.defaultGoal = builder.defaultGoal;
        this.directory = builder.directory;
        this.finalName = builder.finalName;
        this.filters = ofNullable(builder.filters)
                .map(Collections::unmodifiableList)
                .orElse(null);
        this.resources = builder.resources;
        this.testResources = builder.testResources;
        this.pluginManagement = ofNullable(builder.pluginManagementBuilder)
                .map(MavenPluginManagement.Builder::build)
                .orElse(null);
        this.plugins = builder.plugins;
    }

    public String getDefaultGoal() {
        return defaultGoal;
    }

    public String getDirectory() {
        return directory;
    }

    public String getFinalName() {
        return finalName;
    }

    public List<String> getFilters() {
        return filters;
    }

    public MavenResourceContainer getResources() {
        return resources;
    }

    public MavenResourceContainer getTestResources() {
        return testResources;
    }

    public MavenPluginManagement getPluginManagement() {
        return pluginManagement;
    }

    public MavenPluginContainer getPlugins() {
        return plugins;
    }

    public static class Builder {

        private String defaultGoal;

        private String directory;

        private String finalName;

        private List<String> filters;

        private MavenResourceContainer resources;

        private MavenResourceContainer testResources;

        private MavenPluginManagement.Builder pluginManagementBuilder;

        private MavenPluginContainer plugins;

        protected Builder() {
        }

        public MavenProfileBuild.Builder defaultGoal(String defaultGoal) {
            this.defaultGoal = defaultGoal;
            return this;
        }

        public MavenProfileBuild.Builder directory(String directory) {
            this.directory = directory;
            return this;
        }

        public MavenProfileBuild.Builder finalName(String finalName) {
            this.finalName = finalName;
            return this;
        }

        public MavenProfileBuild.Builder filter(String filter) {
            if(this.filters == null){
                this.filters = new LinkedList<>();
            }
            this.filters.add(filter);
            return this;
        }

        public MavenProfileBuild.Builder resources(Consumer<MavenResourceContainer> resources) {
            if(this.resources == null){
                this.resources = new MavenResourceContainer();
            }
            resources.accept(this.resources);
            return this;
        }

        public MavenProfileBuild.Builder testResources(Consumer<MavenResourceContainer> testResources) {
            if(this.testResources == null){
                this.testResources = new MavenResourceContainer();
            }
            testResources.accept(this.testResources);
            return this;
        }

        public MavenProfileBuild.Builder pluginManagement(Consumer<MavenPluginManagement.Builder> pluginManagement) {
            if(this.pluginManagementBuilder == null){
                this.pluginManagementBuilder = new MavenPluginManagement.Builder();
            }
            pluginManagement.accept(this.pluginManagementBuilder);
            return this;
        }

        public MavenProfileBuild.Builder plugins(Consumer<MavenPluginContainer> plugins) {
            if(this.plugins == null){
                this.plugins = new MavenPluginContainer();
            }
            plugins.accept(this.plugins);
            return this;
        }

        public MavenProfileBuild build() {
            return new MavenProfileBuild(this);
        }
    }
}
