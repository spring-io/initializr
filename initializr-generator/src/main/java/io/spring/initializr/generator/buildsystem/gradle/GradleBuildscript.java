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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@code buildscript} section of a {@link GradleBuild}.
 *
 * @author Andy Wilkinson
 */
public class GradleBuildscript {

	private final List<String> dependencies;

	private final Map<String, String> ext;

	protected GradleBuildscript(Builder builder) {
		this.dependencies = Collections.unmodifiableList(new ArrayList<>(builder.dependencies));
		this.ext = Collections.unmodifiableMap(new LinkedHashMap<>(builder.ext));
	}

	/**
	 * Return the dependencies required by this build.
	 * @return the dependencies.
	 */
	public List<String> getDependencies() {
		return this.dependencies;
	}

	/**
	 * Return additional properties this build uses.
	 * @return build properties
	 */
	public Map<String, String> getExt() {
		return this.ext;
	}

	/**
	 * Builder for {@link GradleBuildscript}.
	 */
	public static class Builder {

		private final List<String> dependencies = new ArrayList<>();

		private final Map<String, String> ext = new LinkedHashMap<>();

		public Builder dependency(String coordinates) {
			this.dependencies.add(coordinates);
			return this;
		}

		/**
		 * Set a {@code ext} property.
		 * @param name the name of the property
		 * @param value the value of the property
		 * @return this for method chaining
		 */
		public Builder ext(String name, String value) {
			this.ext.put(name, value);
			return this;
		}

		/**
		 * Build a {@link GradleBuildscript} with the current state of this builder.
		 * @return a {@link GradleBuildscript}
		 */
		public GradleBuildscript build() {
			return new GradleBuildscript(this);
		}

	}

}
