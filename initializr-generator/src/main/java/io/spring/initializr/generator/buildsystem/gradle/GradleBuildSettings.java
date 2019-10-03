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

package io.spring.initializr.generator.buildsystem.gradle;

import io.spring.initializr.generator.buildsystem.BuildSettings;

/**
 * Gradle-specific {@linkplain BuildSettings build settings}.
 *
 * @author Stephane Nicoll
 */
public class GradleBuildSettings extends BuildSettings {

	private final String sourceCompatibility;

	protected GradleBuildSettings(Builder builder) {
		super(builder);
		this.sourceCompatibility = builder.sourceCompatibility;
	}

	/**
	 * Return the java version compatibility to use when compiling Java source.
	 * @return the java version to use for source.
	 */
	public String getSourceCompatibility() {
		return this.sourceCompatibility;
	}

	/**
	 * Builder for {@link GradleBuildSettings}.
	 */
	public static class Builder extends BuildSettings.Builder<Builder> {

		private String sourceCompatibility;

		/**
		 * Set the java version compatibility to use when compiling Java source.
		 * @param sourceCompatibility java version compatibility
		 * @return this for method chaining
		 */
		public Builder sourceCompatibility(String sourceCompatibility) {
			this.sourceCompatibility = sourceCompatibility;
			return self();
		}

		/**
		 * Build a {@link GradleBuildSettings} with the current state of this builder.
		 * @return a {@link GradleBuildSettings}
		 */
		public GradleBuildSettings build() {
			return new GradleBuildSettings(this);
		}

	}

}
