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

/**
 * A {@link MavenProfile profile} activation in a {@link MavenBuild}.
 *
 * @author Stephane Nicoll
 */
public class MavenProfileActivation {

	private final Boolean activeByDefault;

	private final String jdk;

	private final Os os;

	private final Property property;

	private final File file;

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
	public Boolean getActiveByDefault() {
		return this.activeByDefault;
	}

	/**
	 * Specify the JDK(s) that should match for the profile to be activated, or
	 * {@code null} to not enable the profile based on the JDK.
	 * @return the jdk (or jdks range) that should match or {@code null}
	 */
	public String getJdk() {
		return this.jdk;
	}

	/**
	 * Return the operating system activation settings, or {@code null} to not enable the
	 * profile based on the OS.
	 * @return the operating system activation settings or {@code null}
	 */
	public Os getOs() {
		return this.os;
	}

	/**
	 * Return the property to match to enable the profile, or {@code null} to not enable
	 * the profile based on a property.
	 * @return the property to match or {@code null}
	 */
	public Property getProperty() {
		return this.property;
	}

	/**
	 * Return the file activation settings, or {@code null} to not enable the profile
	 * based on the presence or absence of a file.
	 * @return the file activation settings or {@code null}
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 * Operating System activation settings.
	 */
	public static final class Os {

		private final String name;

		private final String family;

		private final String arch;

		private final String version;

		Os(String name, String family, String arch, String version) {
			this.name = name;
			this.family = family;
			this.arch = arch;
			this.version = version;
		}

		/**
		 * Return the name of the OS to match or {@code null}.
		 * @return the name of the OS
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Return the family of OS to match or {@code null}. Can be for instance
		 * {@code mac}, {@code windows}, {@code unix}, {@code os/400}, etc.
		 * @return the family of OS
		 */
		public String getFamily() {
			return this.family;
		}

		/**
		 * Return the cpu architecture of the OS to match or {@code null}.
		 * @return the cpu architecture of the OS
		 */
		public String getArch() {
			return this.arch;
		}

		/**
		 * Return the version of the OS to match or {@code null}.
		 * @return the version of the OS
		 */
		public String getVersion() {
			return this.version;
		}

	}

	/**
	 * Property activation settings.
	 */
	public static final class Property {

		private final String name;

		private final String value;

		Property(String name, String value) {
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
		public String getValue() {
			return this.value;
		}

	}

	/**
	 * File activation settings.
	 */
	public static final class File {

		private final String exists;

		private final String missing;

		File(String exists, String missing) {
			this.missing = missing;
			this.exists = exists;
		}

		/**
		 * Return the file that should exists for the profile to match or {@code null}.
		 * @return the file that should exist
		 */
		public String getExists() {
			return this.exists;
		}

		/**
		 * Return the file that should be missing for the profile to match or
		 * {@code null}.
		 * @return the file that should be missing
		 */
		public String getMissing() {
			return this.missing;
		}

	}

	/**
	 * Builder for {@link MavenProfileActivation}.
	 */
	public static class Builder {

		private Boolean activeByDefault;

		private String jdk;

		private Os os;

		private Property property;

		private String fileExists;

		private String fileMissing;

		protected Builder() {
		}

		/**
		 * Specify if the profile should be enabled if no profile is active.
		 * @param activeByDefault whether to enable the profile is no profile is active
		 * @return this for method chaining
		 */
		public Builder activeByDefault(Boolean activeByDefault) {
			this.activeByDefault = activeByDefault;
			return this;
		}

		/**
		 * Specify the JDK(s) to match to enable the profile. Can be a JDK value or an
		 * OSGi range.
		 * @param jdk the jdk (or JDKs range) to match
		 * @return this for method chaining
		 */
		public Builder jdk(String jdk) {
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
		public Builder os(String name, String family, String arch, String version) {
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
		public Builder property(String name, String value) {
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
		public Builder fileExists(String existingFile) {
			this.fileExists = existingFile;
			return this;
		}

		/**
		 * Specify the file that should be missing to enable the profile.
		 * @param missingFile the file that should be missing
		 * @return this for method chaining
		 */
		public Builder fileMissing(String missingFile) {
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
