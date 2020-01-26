package io.spring.initializr.generator.buildsystem.maven;

public class MavenProfileActivationFile {
    private final String missing;

    private final String exists;

    protected MavenProfileActivationFile(Builder builder) {
        this.missing = builder.missing;
        this.exists = builder.exists;
    }

    public String getMissing() {
        return missing;
    }

    public String getExists() {
        return exists;
    }

    public static class Builder {

        private String missing;

        private String exists;

        protected Builder() {

        }

        public MavenProfileActivationFile.Builder missing(String missing) {
            this.missing = missing;
            return this;
        }

        public MavenProfileActivationFile.Builder exists(String exists) {
            this.exists = exists;
            return this;
        }

        public MavenProfileActivationFile build() {
            return new MavenProfileActivationFile(this);
        }
    }
}
