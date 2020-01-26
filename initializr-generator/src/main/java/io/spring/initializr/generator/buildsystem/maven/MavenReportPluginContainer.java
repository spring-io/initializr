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

import io.spring.initializr.generator.buildsystem.maven.MavenReportPlugin.Builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MavenReportPluginContainer {

	private final Map<String, Builder> reportPlugins = new LinkedHashMap<>();

	public boolean isEmpty() {
		return this.reportPlugins.isEmpty();
	}

	public boolean has(String groupId, String artifactId) {
		return this.reportPlugins.containsKey(pluginKey(groupId, artifactId));
	}

	public Stream<MavenReportPlugin> values() {
		return this.reportPlugins.values().stream().map(Builder::build);
	}

	public MavenReportPluginContainer add(String groupId, String artifactId) {
		addReportPlugin(groupId, artifactId);
		return this;
	}

	public MavenReportPluginContainer add(String groupId, String artifactId, Consumer<Builder> plugin) {
		plugin.accept(addReportPlugin(groupId, artifactId));
		return this;
	}

	private Builder addReportPlugin(String groupId, String artifactId) {
		return this.reportPlugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(pluginId) -> new Builder(groupId, artifactId));
	}

	public boolean remove(String groupId, String artifactId) {
		return this.reportPlugins.remove(pluginKey(groupId, artifactId)) != null;
	}

	private String pluginKey(String groupId, String artifactId) {
		return String.format("%s:%s", groupId, artifactId);
	}

}
