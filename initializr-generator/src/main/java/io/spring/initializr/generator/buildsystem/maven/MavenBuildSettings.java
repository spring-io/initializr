/*
 * Copyright 2012-2019 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.BuildSettings;
import io.spring.initializr.generator.packaging.Packaging;

/**
 * Maven {@link BuildSettings}.
 *
 * @author Stephane Nicoll
 */
public class MavenBuildSettings extends BuildSettings {

	private final MavenParent parent;

	private final String packaging;

	private final String name;

	private final String description;

	private final String sourceDirectory;

	private final String testSourceDirectory;

	private final String finalName;

	protected MavenBuildSettings(Builder builder) {
		super(builder);
		this.parent = builder.parent;
		this.packaging = builder.packaging;
		this.name = builder.name;
		this.description = builder.description;
		this.sourceDirectory = builder.sourceDirectory;
		this.testSourceDirectory = builder.testSourceDirectory;
		this.finalName = builder.finalName;
	}

	/**
	 * Return the {@link MavenParent} to use or {@code null} if this project has no
	 * parent.
	 * @return the parent pom or {@code null}
	 */
	public MavenParent getParent() {
		return this.parent;
	}

	/**
	 * Return the {@code packaging} to use or {@code null} to use the default {@code jar}
	 * packaging.
	 * @return the packaging to use
	 */
	public String getPackaging() {
		return this.packaging;
	}

	/**
	 * Return a simple name for the project.
	 * @return the name of the project or {@code null}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return a human readable description of the project.
	 * @return the description of the project or {@code null}
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Return the location of main source code. Can use Maven properties such as
	 * {@code ${basedir}}.
	 * @return the location of main source code or {@code null} to use the default
	 */
	public String getSourceDirectory() {
		return this.sourceDirectory;
	}

	/**
	 * Return the location of test source code. Can use Maven properties such as
	 * {@code ${basedir}}.
	 * @return the location of test source code or {@code null} to use the default
	 */
	public String getTestSourceDirectory() {
		return this.testSourceDirectory;
	}

	/**
	 * Return the final name of the compiled artifact.
	 * @return the final name
	 */
	public String getFinalName() {
		return this.finalName;
	}

	/**
	 * Builder for a Maven dependency.
	 *
	 * @see MavenDependency#withCoordinates(String, String)
	 */
	public static class Builder extends BuildSettings.Builder<Builder> {

		private MavenParent parent;

		private String packaging;

		private String name;

		private String description;

		private String sourceDirectory;

		private String testSourceDirectory;

		private String finalName;

		public Builder() {
		}

		/**
		 * Set the coordinates of the project.
		 * @param groupId the group ID of the project
		 * @param artifactId the artifact ID of the project
		 * @return this for method chaining
		 */
		public Builder coordinates(String groupId, String artifactId) {
			return group(groupId).artifact(artifactId);
		}

		/**
		 * Set the coordinates of the parent.
		 * @param groupId the groupID of the parent
		 * @param artifactId the artifactID of the parent
		 * @param version the version of the parent
		 * @return this for method chaining
		 */
		public Builder parent(String groupId, String artifactId, String version) {
			this.parent = new MavenParent(groupId, artifactId, version);
			return self();
		}

		/**
		 * Set the packaging of the project.
		 * @param packaging the packaging
		 * @return this for method chaining
		 * @see Packaging
		 */
		public Builder packaging(String packaging) {
			this.packaging = packaging;
			return self();
		}

		/**
		 * Set the name of the project.
		 * @param name the name of the project
		 * @return this for method chaining
		 */
		public Builder name(String name) {
			this.name = name;
			return self();
		}

		/**
		 * Set a human readable description of the project.
		 * @param description the description of the project
		 * @return this for method chaining
		 */
		public Builder description(String description) {
			this.description = description;
			return self();
		}

		/**
		 * Set the the location of main source code. Can use Maven properties such as
		 * {@code ${basedir}}.
		 * @param sourceDirectory the location of main source code or {@code null} to use
		 * the default
		 * @return this for method chaining
		 */
		public Builder sourceDirectory(String sourceDirectory) {
			this.sourceDirectory = sourceDirectory;
			return self();
		}

		/**
		 * Set the the location of test source code. Can use Maven properties such as
		 * {@code ${basedir}}.
		 * @param testSourceDirectory the location of test source code or {@code null} to
		 * use the default
		 * @return this for method chaining
		 */
		public Builder testSourceDirectory(String testSourceDirectory) {
			this.testSourceDirectory = testSourceDirectory;
			return self();
		}

		/**
		 * Set the finalName of the artifact as a child tag in the &lt;build&gt; tag.
		 * @param finalName the final name of the artifact after compiling
		 * @return this for method chaining
		 */
		public Builder finalName(String finalName) {
			this.finalName = finalName;
			return self();
		}

		@Override
		public MavenBuildSettings build() {
			return new MavenBuildSettings(this);
		}

	}

}
