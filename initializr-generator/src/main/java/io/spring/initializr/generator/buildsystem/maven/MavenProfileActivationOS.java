package io.spring.initializr.generator.buildsystem.maven;

public class MavenProfileActivationOS {
    private final String name;

    private final String family;

    private final String arch;

    private final String version;

    public MavenProfileActivationOS(Builder builder) {
        this.name = builder.name;
        this.family = builder.family;
        this.arch = builder.arch;
        this.version = builder.version;
    }

    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }

    public String getArch() {
        return arch;
    }

    public String getVersion() {
        return version;
    }

    public static class Builder {

        private String name;

        private String family;

        private String arch;

        private String version;

        protected Builder() {

        }

        public MavenProfileActivationOS.Builder name(String name) {
            this.name = name;
            return this;
        }

        public MavenProfileActivationOS.Builder family(String family) {
            this.family = family;
            return this;
        }

        public MavenProfileActivationOS.Builder arch(String arch) {
            this.arch = arch;
            return this;
        }

        public MavenProfileActivationOS.Builder version(String version) {
            this.version = version;
            return this;
        }

        public MavenProfileActivationOS build() {
            return new MavenProfileActivationOS(this);
        }
    }
}
