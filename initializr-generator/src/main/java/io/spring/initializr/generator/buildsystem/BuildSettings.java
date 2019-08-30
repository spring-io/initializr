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

package io.spring.initializr.generator.buildsystem;

/**
 * General build settings.
 *
 * @author Stephane Nicoll
 */
public class BuildSettings {

	private final String group;

	private final String artifact;

	private final String version;

	protected BuildSettings(Builder<?> builder) {
		this.group = builder.group;
		this.artifact = builder.artifact;
		this.version = builder.version;
	}

	/**
	 * Return the identifier of the group for the project.
	 * @return the group identifier or {@code null}
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * Return the identifier of the project.
	 * @return the project identifier or {@code null}
	 */
	public String getArtifact() {
		return this.artifact;
	}

	/**
	 * Return the version of the project.
	 * @return the project version or {@code null}
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Builder for build settings.
	 *
	 * @param <B> builder type
	 */
	public abstract static class Builder<B extends Builder> {

		private String group;

		private String artifact;

		private String version = "0.0.1-SNAPSHOT";

		protected Builder() {
		}

		public B group(String group) {
			this.group = group;
			return self();
		}

		public B artifact(String artifact) {
			this.artifact = artifact;
			return self();
		}

		public B version(String version) {
			this.version = version;
			return self();
		}

		@SuppressWarnings("unchecked")
		protected B self() {
			return (B) this;
		}

		public BuildSettings build() {
			return new BuildSettings(this);
		}

	}

}
