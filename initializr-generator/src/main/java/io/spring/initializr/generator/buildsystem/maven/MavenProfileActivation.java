package io.spring.initializr.generator.buildsystem.maven;

import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

public class MavenProfileActivation {
    private final Boolean activeByDefault;

    private final String jdk;

    private final MavenProfileActivationOS os;

    private final MavenProfileActivationProperty property;

    private final MavenProfileActivationFile file;

    protected MavenProfileActivation(Builder builder) {
        this.activeByDefault = builder.activeByDefault;
        this.jdk = builder.jdk;
        this.os = ofNullable(builder.osBuilder)
                .map(MavenProfileActivationOS.Builder::build)
                .orElse(null);
        this.property = ofNullable(builder.propertyBuilder)
                .map(MavenProfileActivationProperty.Builder::build)
                .orElse(null);
        this.file = ofNullable(builder.fileBuilder)
                .map(MavenProfileActivationFile.Builder::build)
                .orElse(null);
    }

    public Boolean getActiveByDefault() {
        return activeByDefault;
    }

    public String getJdk() {
        return jdk;
    }

    public MavenProfileActivationOS getOs() {
        return os;
    }

    public MavenProfileActivationProperty getProperty() {
        return property;
    }

    public MavenProfileActivationFile getFile() {
        return file;
    }

    public static class Builder {

        private Boolean activeByDefault;

        private String jdk;

        private MavenProfileActivationOS.Builder osBuilder;

        private MavenProfileActivationProperty.Builder propertyBuilder;

        private MavenProfileActivationFile.Builder fileBuilder;

        protected Builder() {
        }

        public MavenProfileActivation.Builder activeByDefault(boolean activeByDefault) {
            this.activeByDefault = activeByDefault;
            return this;
        }

        public MavenProfileActivation.Builder jdk(String jdk) {
            this.jdk = jdk;
            return this;
        }

        public MavenProfileActivation.Builder os(Consumer<MavenProfileActivationOS.Builder> os) {
            if (this.osBuilder == null) {
                this.osBuilder = new MavenProfileActivationOS.Builder();
            }
            os.accept(this.osBuilder);
            return this;
        }

        public MavenProfileActivation.Builder property(Consumer<MavenProfileActivationProperty.Builder> property) {
            if (this.propertyBuilder == null) {
                this.propertyBuilder = new MavenProfileActivationProperty.Builder();
            }
            property.accept(this.propertyBuilder);
            return this;
        }

        public MavenProfileActivation.Builder file(Consumer<MavenProfileActivationFile.Builder> file) {
            if (this.fileBuilder == null) {
                this.fileBuilder = new MavenProfileActivationFile.Builder();
            }
            file.accept(this.fileBuilder);
            return this;
        }

        public MavenProfileActivation build() {
            return new MavenProfileActivation(this);
        }
    }
}
