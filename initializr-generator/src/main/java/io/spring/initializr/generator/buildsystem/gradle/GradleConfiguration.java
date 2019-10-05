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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A custom Gradle configuration that can be associated to a {@linkplain GradleBuild
 * build}.
 *
 * @author Stephane Nicoll
 */
public class GradleConfiguration {

	private final String name;

	private final Set<String> extendsFrom;

	protected GradleConfiguration(Builder builder) {
		this.name = builder.name;
		this.extendsFrom = Collections.unmodifiableSet(new LinkedHashSet<>(builder.extendsFrom));
	}

	/**
	 * Return the name of the configuration.
	 * @return the configuration name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the configuration names that this configuration should extend from.
	 * @return the configuration names that this configuration should extend from
	 */
	public Set<String> getExtendsFrom() {
		return this.extendsFrom;
	}

	public static class Builder {

		private final String name;

		private final Set<String> extendsFrom = new LinkedHashSet<>();

		protected Builder(String name) {
			this.name = name;
		}

		/**
		 * Add a configuration name that this configuration should extend from. Does
		 * nothing if such configuration is already present.
		 * @param configurationName the name of a configuration this configuration should
		 * extend from
		 * @return this for method chaining
		 */
		public Builder extendsFrom(String configurationName) {
			this.extendsFrom.add(configurationName);
			return this;
		}

		/**
		 * Build a {@link GradleConfiguration} with the current state of this builder.
		 * @return a {@link GradleConfiguration}
		 */
		public GradleConfiguration build() {
			return new GradleConfiguration(this);
		}

	}

}
