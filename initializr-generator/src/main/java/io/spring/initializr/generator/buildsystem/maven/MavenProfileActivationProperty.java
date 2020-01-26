package io.spring.initializr.generator.buildsystem.maven;

public class MavenProfileActivationProperty {
    private final String name;

    private final String value;

    protected MavenProfileActivationProperty(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {

        private String name;

        private String value;

        protected Builder() {

        }

        public MavenProfileActivationProperty.Builder name(String name) {
            this.name = name;
            return this;
        }

        public MavenProfileActivationProperty.Builder value(String value) {
            this.value = value;
            return this;
        }

        public MavenProfileActivationProperty build() {
            return new MavenProfileActivationProperty(this);
        }
    }
}
