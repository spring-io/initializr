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

/**
 * A plugin in a {@link GradleBuild}.
 *
 * @author Andy Wilkinson
 */
public class GradlePlugin {

	private final String id;

	private final boolean apply;

	/**
	 * Create a new instance.
	 * @param id the id of the plugin
	 * @param apply whether the plugin should be applied or not
	 */
	public GradlePlugin(String id, boolean apply) {
		this.id = id;
		this.apply = apply;
	}

	/**
	 * Return the plugin identifier.
	 * @return the plugin id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Return whether the plugin should be applied.
	 * @return {@code true} to use {@code apply}, {@code false} to register it
	 */
	public boolean isApply() {
		return this.apply;
	}

}
