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

import java.util.function.Consumer;

public class MavenPluginManagement {

	private final MavenPluginContainer plugins;

	protected MavenPluginManagement(Builder builder) {
		this.plugins = builder.plugins;
	}

	public MavenPluginContainer getPlugins() {
		return plugins;
	}

	public static class Builder {

		private MavenPluginContainer plugins = new MavenPluginContainer();

		protected Builder() {
		}

		public MavenPluginManagement.Builder plugins(Consumer<MavenPluginContainer> plugins) {
			plugins.accept(this.plugins);
			return this;
		}

		public MavenPluginManagement build() {
			return new MavenPluginManagement(this);
		}

	}

}
