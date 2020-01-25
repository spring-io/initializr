package io.spring.initializr.generator.buildsystem.maven;

import io.spring.initializr.generator.buildsystem.DependencyContainer;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MavenProfileBuild {
    private final String defaultGoal;

    private final String directory;

    private final String finalName;

    private final List<String> filters;

    private final MavenResourceContainer resources;

    private final MavenResourceContainer testResources;

    private final MavenPluginManagement pluginManagement;

    private final MavenPluginContainer plugins;

    private final DependencyContainer dependencies;

    protected MavenProfileBuild(Builder builder) {
        this.defaultGoal = builder.defaultGoal;
        this.directory = builder.directory;
        this.finalName = builder.finalName;
        this.filters = builder.filters;
        this.resources = builder.resources;
        this.testResources = builder.testResources;
        this.pluginManagement = (builder.pluginManagementBuilder == null) ? null : builder.pluginManagementBuilder.build();
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

        private List<String> filters = new LinkedList<>();

        private MavenResourceContainer resources = new MavenResourceContainer();

        private MavenResourceContainer testResources = new MavenResourceContainer();

        private MavenPluginManagement.Builder pluginManagementBuilder = new MavenPluginManagement.Builder();

        private MavenPluginContainer plugins = new MavenPluginContainer();

        protected Builder() {
        }

        public MavenProfileBuild.Builder activeByDefault(String defaultGoal) {
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
            this.filters.add(filter);
            return this;
        }

        public MavenProfileBuild.Builder resources(Consumer<MavenResourceContainer> resources) {
            resources.accept(this.resources);
            return this;
        }

        public MavenProfileBuild.Builder testResources(Consumer<MavenResourceContainer> testResources) {
            testResources.accept(this.testResources);
            return this;
        }

        public MavenProfileBuild.Builder pluginManagement(Consumer<MavenPluginManagement.Builder> pluginManagement) {
            pluginManagement.accept(this.pluginManagementBuilder);
            return this;
        }

        public MavenProfileBuild.Builder plugins(Consumer<MavenPluginContainer> plugins) {
            plugins.accept(this.plugins);
            return this;
        }

        public MavenProfileBuild build() {
            return new MavenProfileBuild(this);
        }
    }
}
