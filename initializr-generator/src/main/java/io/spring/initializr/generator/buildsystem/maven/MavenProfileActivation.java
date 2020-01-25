package io.spring.initializr.generator.buildsystem.maven;

import java.util.function.Consumer;

public class MavenProfileActivation {
    private final Boolean activeByDefault;

    private final String jdk;

    private final MavenProfileActivationOS os;

    private final MavenProfileActivationProperty property;

    private final MavenProfileActivationFile file;

    public MavenProfileActivation(Builder builder) {
        this.activeByDefault = builder.activeByDefault;
        this.jdk = builder.jdk;
        this.os = (builder.osBuilder == null) ? null : builder.osBuilder.build();
        this.property = (builder.propertyBuilder == null) ? null : builder.propertyBuilder.build();
        this.file = (builder.fileBuilder == null) ? null : builder.fileBuilder.build();
    }

    public Boolean getActiveByDefault() {
        return activeByDefault;
    }

    public String getJdk() {
        return jdk;
    }


    public static class Builder {

        private Boolean activeByDefault = false;

        private String jdk;

        private MavenProfileActivationOS.Builder osBuilder;

        private MavenProfileActivationProperty.Builder propertyBuilder;

        private MavenProfileActivationFile.Builder fileBuilder;

        public Builder() {
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
