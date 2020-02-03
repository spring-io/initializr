package io.spring.initializr.generator.buildsystem.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A {@code <configuration>} or {@code <properties>} that allow every xml structure on a different parts of the pom like {@link MavenPlugin.Execution}, {@link MavenPlugin}. TODO
 *
 * @author Andy Wilkinson
 * @author Olga Maciaszek-Sharma
 * @author Daniel Andres Pelaez Lopez
 */
public final class MavenConfiguration {

    private final List<Setting> settings;

    MavenConfiguration(List<Setting> settings) {
        this.settings = Collections.unmodifiableList(settings);
    }

    /**
     * Return the {@linkplain Setting settings} of the configuration.
     * @return the settings
     */
    public List<Setting> getSettings() {
        return this.settings;
    }

    /**
     * A setting in a {@link MavenConfiguration}.
     */
    public static final class Setting {

        private final String name;

        private final Object value;

        private Setting(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        /**
         * Return the name of the configuration item.
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Return the value. Can be a nested {@link MavenConfiguration}.
         * @return a simple value or a nested configuration
         */
        public Object getValue() {
            return this.value;
        }

    }

    /**
     * Builder for a {@link MavenConfiguration}.
     */
    public static class Builder {

        private final List<Setting> settings = new ArrayList<>();

        /**
         * Add the specified parameter with a single value.
         * @param name the name of the parameter
         * @param value the single value of the parameter
         * @return this for method chaining
         */
        public Builder add(String name, String value) {
            this.settings.add(new Setting(name, value));
            return this;
        }

        /**
         * Configure the parameter with the specified {@code name}.
         * @param name the name of the parameter
         * @param consumer a consumer to further configure the parameter
         * @return this for method chaining
         * @throws IllegalArgumentException if a parameter with the same name is
         * registered with a single value
         */
        public Builder configure(String name, Consumer<Builder> consumer) {
            Object value = this.settings.stream().filter((candidate) -> candidate.getName().equals(name)).findFirst()
                    .orElseGet(() -> {
                        Setting nestedSetting = new Setting(name, new Builder());
                        this.settings.add(nestedSetting);
                        return nestedSetting;
                    }).getValue();
            if (!(value instanceof Builder)) {
                throw new IllegalArgumentException(String.format(
                        "Could not customize parameter '%s', a single value %s is already registered", name, value));
            }
            Builder nestedConfiguration = (Builder) value;
            consumer.accept(nestedConfiguration);
            return this;
        }

        /**
         * Build a {@link MavenConfiguration} with the current state of this builder.
         * @return a {@link MavenConfiguration}
         */
        MavenConfiguration build() {
            return new MavenConfiguration(this.settings.stream().map((entry) -> resolve(entry.getName(), entry.getValue()))
                    .collect(Collectors.toList()));
        }

        private Setting resolve(String key, Object value) {
            if (value instanceof Builder) {
                List<Setting> values = ((Builder) value).settings.stream()
                        .map((entry) -> resolve(entry.getName(), entry.getValue())).collect(Collectors.toList());
                return new Setting(key, values);
            }
            else {
                return new Setting(key, value);
            }
        }

    }
}
