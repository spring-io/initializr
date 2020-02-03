/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.buildsystem.maven;

import java.util.Optional;
import java.util.function.Consumer;

public class MavenProfileActivation {

	private final Boolean activeByDefault;

	private final String jdk;

	private final MavenProfileActivationOS os;

	private final MavenProfileActivationProperty property;

	private final MavenProfileActivationFile file;

	protected MavenProfileActivation(Builder builder) {
		this.activeByDefault = builder.activeByDefault;
		this.jdk = builder.jdk;
		this.os = Optional.ofNullable(builder.osBuilder).map(MavenProfileActivationOS.Builder::build).orElse(null);
		this.property = Optional.ofNullable(builder.propertyBuilder).map(MavenProfileActivationProperty.Builder::build)
				.orElse(null);
		this.file = Optional.ofNullable(builder.fileBuilder).map(MavenProfileActivationFile.Builder::build)
				.orElse(null);
	}

	public Boolean getActiveByDefault() {
		return this.activeByDefault;
	}

	public String getJdk() {
		return this.jdk;
	}

	public MavenProfileActivationOS getOs() {
		return this.os;
	}

	public MavenProfileActivationProperty getProperty() {
		return this.property;
	}

	public MavenProfileActivationFile getFile() {
		return this.file;
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
