package io.spring.initializr.generator.buildsystem.maven;

import java.util.function.Consumer;

public class MavenPluginManagement {
    private final MavenPluginContainer plugins;

    protected MavenPluginManagement(Builder builder) {
        this.plugins = builder.plugins;
    }

    public MavenPluginContainer getPlugins() {
        return plugins;
    }

    public static class Builder {

        private MavenPluginContainer plugins = new MavenPluginContainer();

        protected Builder() {
        }

        public MavenPluginManagement.Builder plugins(Consumer<MavenPluginContainer> plugins) {
            plugins.accept(this.plugins);
            return this;
        }

        public MavenPluginManagement build() {
            return new MavenPluginManagement(this);
        }
    }
}
