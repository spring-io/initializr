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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;

/**
 * Maven build for a project.
 *
 * @author Andy Wilkinson
 */
public class MavenBuild extends Build {

	private MavenParent parent;

	private String name;

	private String description;

	private String sourceDirectory;

	private String testSourceDirectory;

	private final Map<String, String> properties = new TreeMap<>();

	private final Map<String, MavenPlugin> plugins = new LinkedHashMap<>();

	private final Map<String, ResourceBuilder> resources = new LinkedHashMap<>();

	private String packaging;

	public MavenBuild(BuildItemResolver buildItemResolver) {
		super(buildItemResolver);
	}

	public MavenBuild() {
		this(null);
	}

	public MavenParent parent(String groupId, String artifactId, String version) {
		this.parent = new MavenParent(groupId, artifactId, version);
		return this.parent;
	}

	public MavenParent getParent() {
		return this.parent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setProperty(String key, String value) {
		this.properties.put(key, value);
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	public String getSourceDirectory() {
		return this.sourceDirectory;
	}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public String getTestSourceDirectory() {
		return this.testSourceDirectory;
	}

	public void setTestSourceDirectory(String testSourceDirectory) {
		this.testSourceDirectory = testSourceDirectory;
	}

	public MavenPlugin plugin(String groupId, String artifactId) {
		return this.plugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(id) -> new MavenPlugin(groupId, artifactId));
	}

	public MavenPlugin plugin(String groupId, String artifactId, String version) {
		MavenPlugin mavenPlugin = this.plugins.computeIfAbsent(pluginKey(groupId, artifactId),
				(id) -> new MavenPlugin(groupId, artifactId));
		mavenPlugin.setVersion(version);
		return mavenPlugin;
	}

	private String pluginKey(String groupId, String artifactId) {
		return String.format("%s:%s", groupId, artifactId);
	}

	public List<MavenPlugin> getPlugins() {
		return Collections.unmodifiableList(new ArrayList<>(this.plugins.values()));
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getPackaging() {
		return this.packaging;
	}

	public void resource(String directory, Consumer<ResourceBuilder> customizer) {
		customizer.accept(this.resources.computeIfAbsent(directory, (key) -> new ResourceBuilder(directory)));
	}

	public List<Resource> getResources() {
		return this.resources.values().stream().map(ResourceBuilder::build).collect(Collectors.toList());
	}

	/**
	 *
	 * Builder to create a {@link Resource}.
	 *
	 *
	 */
	public static final class ResourceBuilder {

		private String directory;

		private String targetPath;

		private boolean filtering;

		private List<String> includes = new ArrayList<>();

		private List<String> excludes = new ArrayList<>();

		public ResourceBuilder(String directory) {
			this.directory = directory;
		}

		Resource build() {
			return new Resource(this.directory, this.targetPath, this.filtering, this.includes, this.excludes);
		}

		public ResourceBuilder include(String... includes) {
			this.includes = Arrays.asList(includes);
			return this;
		}

		public ResourceBuilder targetPath(String targetPath) {
			this.targetPath = targetPath;
			return this;
		}

		public ResourceBuilder filtering(Boolean filtering) {
			this.filtering = filtering;
			return this;
		}

		public ResourceBuilder excludes(String... excludes) {
			this.excludes = Arrays.asList(excludes);
			return this;
		}

	}

	/**
	 *
	 * An {@code <resource>} of a {@link MavenBuild}.
	 *
	 *
	 */
	public static final class Resource {

		private String directory;

		private String targetPath;

		private boolean filtering;

		private List<String> includes = new ArrayList<>();

		private List<String> excludes = new ArrayList<>();

		public Resource(String directory, String targetPath, boolean filtering, List<String> includes,
				List<String> excludes) {
			super();
			this.directory = directory;
			this.targetPath = targetPath;
			this.filtering = filtering;
			this.includes = includes;
			this.excludes = excludes;
		}

		public String getDirectory() {
			return this.directory;
		}

		public List<String> getIncludes() {
			return this.includes;
		}

		public String getTargetPath() {
			return this.targetPath;
		}

		public boolean isFiltering() {
			return this.filtering;
		}

		public List<String> getExcludes() {
			return this.excludes;
		}

	}

}
