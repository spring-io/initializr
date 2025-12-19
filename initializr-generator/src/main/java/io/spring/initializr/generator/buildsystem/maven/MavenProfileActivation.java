/*
 * Copyright 2012 - present the original author or authors.
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

import org.jspecify.annotations.Nullable;

/**
 * A {@link MavenProfile profile} activation in a {@link MavenBuild}.
 *
 * @author Stephane Nicoll
 */
public class MavenProfileActivation {

	private final @Nullable Boolean activeByDefault;

	private final @Nullable String jdk;

	private final @Nullable Os os;

	private final @Nullable Property property;

	private final @Nullable File file;

	/**
	 * Creates a new instance.
	 * @param builder the builder to use
	 */
	protected MavenProfileActivation(Builder builder) {
		this.activeByDefault = builder.activeByDefault;
		this.jdk = builder.jdk;
		this.os = builder.os;
		this.property = builder.property;
		this.file = (builder.fileExists != null || builder.fileMissing != null)
				? new File(builder.fileExists, builder.fileMissing) : null;
	}

	/**
	 * Specify if this activation has any non-default value.
	 * @return {@code true} if there are no non-default values
	 */
	public boolean isEmpty() {
		return (this.activeByDefault == null && this.jdk == null && this.os == null && this.property == null
				&& this.file == null);
	}

	/**
	 * Specify if the profile should be activated by default, or {@code null} to use the
	 * default value.
	 * @return {@code true} to active the profile if no other profile is active
	 */
	public @Nullable Boolean getActiveByDefault() {
		return this.activeByDefault;
	}

	/**
	 * Specify the JDK(s) that should match for the profile to be activated, or
	 * {@code null} to not enable the profile based on the JDK.
	 * @return the jdk (or jdks range) that should match or {@code null}
	 */
	public @Nullable String getJdk() {
		return this.jdk;
	}

	/**
	 * Return the operating system activation settings, or {@code null} to not enable the
	 * profile based on the OS.
	 * @return the operating system activation settings or {@code null}
	 */
	public @Nullable Os getOs() {
		return this.os;
	}

	/**
	 * Return the property to match to enable the profile, or {@code null} to not enable
	 * the profile based on a property.
	 * @return the property to match or {@code null}
	 */
	public @Nullable Property getProperty() {
		return this.property;
	}

	/**
	 * Return the file activation settings, or {@code null} to not enable the profile
	 * based on the presence or absence of a file.
	 * @return the file activation settings or {@code null}
	 */
	public @Nullable File getFile() {
		return this.file;
	}

	/**
	 * Operating System activation settings.
	 */
	public static final class Os {

		private final @Nullable String name;

		private final @Nullable String family;

		private final @Nullable String arch;

		private final @Nullable String version;

		Os(@Nullable String name, @Nullable String family, @Nullable String arch, @Nullable String version) {
			this.name = name;
			this.family = family;
			this.arch = arch;
			this.version = version;
		}

		/**
		 * Return the name of the OS to match or {@code null}.
		 * @return the name of the OS
		 */
		public @Nullable String getName() {
			return this.name;
		}

		/**
		 * Return the family of OS to match or {@code null}. Can be for instance
		 * {@code mac}, {@code windows}, {@code unix}, {@code os/400}, etc.
		 * @return the family of OS
		 */
		public @Nullable String getFamily() {
			return this.family;
		}

		/**
		 * Return the cpu architecture of the OS to match or {@code null}.
		 * @return the cpu architecture of the OS
		 */
		public @Nullable String getArch() {
			return this.arch;
		}

		/**
		 * Return the version of the OS to match or {@code null}.
		 * @return the version of the OS
		 */
		public @Nullable String getVersion() {
			return this.version;
		}

	}

	/**
	 * Property activation settings.
	 */
	public static final class Property {

		private final String name;

		private final @Nullable String value;

		Property(String name, @Nullable String value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * Return the name of the property.
		 * @return the property name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Return the value of the property.
		 * @return the property value
		 */
		public @Nullable String getValue() {
			return this.value;
		}

	}

	/**
	 * File activation settings.
	 */
	public static final class File {

		private final @Nullable String exists;

		private final @Nullable String missing;

		File(@Nullable String exists, @Nullable String missing) {
			this.missing = missing;
			this.exists = exists;
		}

		/**
		 * Return the file that should exists for the profile to match or {@code null}.
		 * @return the file that should exist
		 */
		public @Nullable String getExists() {
			return this.exists;
		}

		/**
		 * Return the file that should be missing for the profile to match or
		 * {@code null}.
		 * @return the file that should be missing
		 */
		public @Nullable String getMissing() {
			return this.missing;
		}

	}

	/**
	 * Builder for {@link MavenProfileActivation}.
	 */
	public static class Builder {

		private @Nullable Boolean activeByDefault;

		private @Nullable String jdk;

		private @Nullable Os os;

		private @Nullable Property property;

		private @Nullable String fileExists;

		private @Nullable String fileMissing;

		/**
		 * Creates a new instance.
		 */
		protected Builder() {
		}

		/**
		 * Specify if the profile should be enabled if no profile is active.
		 * @param activeByDefault whether to enable the profile is no profile is active
		 * @return this for method chaining
		 */
		public Builder activeByDefault(@Nullable Boolean activeByDefault) {
			this.activeByDefault = activeByDefault;
			return this;
		}

		/**
		 * Specify the JDK(s) to match to enable the profile. Can be a JDK value or an
		 * OSGi range.
		 * @param jdk the jdk (or JDKs range) to match
		 * @return this for method chaining
		 */
		public Builder jdk(@Nullable String jdk) {
			this.jdk = jdk;
			return this;
		}

		/**
		 * Specify the OS to match to enable the profile.
		 * @param name the name of the OS
		 * @param family the family os OS
		 * @param arch the cpu architecture
		 * @param version the version of the OS
		 * @return this for method chaining
		 */
		public Builder os(@Nullable String name, @Nullable String family, @Nullable String arch,
				@Nullable String version) {
			if (name == null && family == null && arch == null && version == null) {
				this.os = null;
			}
			else {
				this.os = new Os(name, family, arch, version);
			}
			return this;
		}

		/**
		 * Specify the property to match to enable the profile.
		 * @param name the name of the property
		 * @param value the value of the property
		 * @return this for method chaining
		 */
		public Builder property(@Nullable String name, @Nullable String value) {
			if (name == null) {
				this.property = null;
			}
			else {
				this.property = new Property(name, value);
			}
			return this;
		}

		/**
		 * Specify the file that should exist to enable the profile.
		 * @param existingFile the file that should exist
		 * @return this for method chaining
		 */
		public Builder fileExists(@Nullable String existingFile) {
			this.fileExists = existingFile;
			return this;
		}

		/**
		 * Specify the file that should be missing to enable the profile.
		 * @param missingFile the file that should be missing
		 * @return this for method chaining
		 */
		public Builder fileMissing(@Nullable String missingFile) {
			this.fileMissing = missingFile;
			return this;
		}

		/**
		 * Create a {@link MavenProfileActivation} with the current state of this builder.
		 * @return a {@link MavenProfileActivation}.
		 */
		public MavenProfileActivation build() {
			return new MavenProfileActivation(this);
		}

	}

}
