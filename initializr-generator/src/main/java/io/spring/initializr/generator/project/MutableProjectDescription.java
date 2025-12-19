/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.project;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.version.Version;
import org.jspecify.annotations.Nullable;

import org.springframework.util.StringUtils;

/**
 * A mutable implementation of {@link ProjectDescription}.
 *
 * @author Andy Wilkinson
 */
public class MutableProjectDescription implements ProjectDescription {

	private @Nullable Version platformVersion;

	private @Nullable BuildSystem buildSystem;

	private @Nullable Packaging packaging;

	private @Nullable Language language;

	private @Nullable ConfigurationFileFormat configurationFileFormat;

	private final Map<String, Dependency> requestedDependencies = new LinkedHashMap<>();

	private @Nullable String groupId;

	private @Nullable String artifactId;

	private @Nullable String version;

	private @Nullable String name;

	private @Nullable String description;

	private @Nullable String applicationName;

	private @Nullable String packageName;

	private @Nullable String baseDirectory;

	/**
	 * Creates a new instance.
	 */
	public MutableProjectDescription() {
	}

	/**
	 * Create a new instance with the state of the specified {@code source}.
	 * @param source the source description to initialize this instance with
	 */
	protected MutableProjectDescription(MutableProjectDescription source) {
		this.platformVersion = source.getPlatformVersion();
		this.buildSystem = source.getBuildSystem();
		this.packaging = source.getPackaging();
		this.language = source.getLanguage();
		this.configurationFileFormat = source.getConfigurationFileFormat();
		this.requestedDependencies.putAll(source.getRequestedDependencies());
		this.groupId = source.getGroupId();
		this.artifactId = source.getArtifactId();
		this.version = source.getVersion();
		this.name = source.getName();
		this.description = source.getDescription();
		this.applicationName = source.getApplicationName();
		this.packageName = source.getPackageName();
		this.baseDirectory = source.getBaseDirectory();
	}

	@Override
	public MutableProjectDescription createCopy() {
		return new MutableProjectDescription(this);
	}

	@Override
	public @Nullable Version getPlatformVersion() {
		return this.platformVersion;
	}

	/**
	 * Sets the platform version.
	 * @param platformVersion the platform version
	 */
	public void setPlatformVersion(@Nullable Version platformVersion) {
		this.platformVersion = platformVersion;
	}

	@Override
	public @Nullable BuildSystem getBuildSystem() {
		return this.buildSystem;
	}

	/**
	 * Sets the build system.
	 * @param buildSystem the build system
	 */
	public void setBuildSystem(@Nullable BuildSystem buildSystem) {
		this.buildSystem = buildSystem;
	}

	@Override
	public @Nullable Packaging getPackaging() {
		return this.packaging;
	}

	/**
	 * Sets the packaging.
	 * @param packaging the packaging
	 */
	public void setPackaging(@Nullable Packaging packaging) {
		this.packaging = packaging;
	}

	@Override
	public @Nullable Language getLanguage() {
		return this.language;
	}

	/**
	 * Sets the configuration file format.
	 * @param configurationFileFormat the configuration file format
	 */
	public void setConfigurationFileFormat(@Nullable ConfigurationFileFormat configurationFileFormat) {
		this.configurationFileFormat = configurationFileFormat;
	}

	@Override
	public @Nullable ConfigurationFileFormat getConfigurationFileFormat() {
		return this.configurationFileFormat;
	}

	/**
	 * Sets the language.
	 * @param language the language
	 */
	public void setLanguage(@Nullable Language language) {
		this.language = language;
	}

	/**
	 * Adds the given dependency.
	 * @param id the id
	 * @param dependency the dependency
	 * @return the added dependency
	 */
	public @Nullable Dependency addDependency(String id, Dependency dependency) {
		return this.requestedDependencies.put(id, dependency);
	}

	/**
	 * Adds the given dependency.
	 * @param id the id
	 * @param builder the dependency builder
	 * @return the added dependency
	 */
	public @Nullable Dependency addDependency(String id, Dependency.Builder<?> builder) {
		return addDependency(id, builder.build());
	}

	/**
	 * Removes the dependency with the given id.
	 * @param id the id
	 * @return the removed dependency
	 */
	public @Nullable Dependency removeDependency(String id) {
		return this.requestedDependencies.remove(id);
	}

	@Override
	public Map<String, Dependency> getRequestedDependencies() {
		return Collections.unmodifiableMap(this.requestedDependencies);
	}

	@Override
	public @Nullable String getGroupId() {
		return this.groupId;
	}

	/**
	 * Sets the group id.
	 * @param groupId the group id
	 */
	public void setGroupId(@Nullable String groupId) {
		this.groupId = groupId;
	}

	@Override
	public @Nullable String getArtifactId() {
		return this.artifactId;
	}

	/**
	 * Sets the artifact id.
	 * @param artifactId the artifact id
	 */
	public void setArtifactId(@Nullable String artifactId) {
		this.artifactId = artifactId;
	}

	@Override
	public @Nullable String getVersion() {
		return this.version;
	}

	/**
	 * Sets the version.
	 * @param version the version
	 */
	public void setVersion(@Nullable String version) {
		this.version = version;
	}

	@Override
	public @Nullable String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 * @param name the name
	 */
	public void setName(@Nullable String name) {
		this.name = name;
	}

	@Override
	public @Nullable String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 * @param description the description
	 */
	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	@Override
	public @Nullable String getApplicationName() {
		return this.applicationName;
	}

	/**
	 * Sets the application name.
	 * @param applicationName the application name
	 */
	public void setApplicationName(@Nullable String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public @Nullable String getPackageName() {
		if (StringUtils.hasText(this.packageName)) {
			return this.packageName;
		}
		if (StringUtils.hasText(this.groupId) && StringUtils.hasText(this.artifactId)) {
			return this.groupId + "." + this.artifactId;
		}
		return null;
	}

	/**
	 * Sets the package name.
	 * @param packageName the package name
	 */
	public void setPackageName(@Nullable String packageName) {
		this.packageName = packageName;
	}

	@Override
	public @Nullable String getBaseDirectory() {
		return this.baseDirectory;
	}

	/**
	 * Sets the base directory.
	 * @param baseDirectory the base directory
	 */
	public void setBaseDirectory(@Nullable String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

}
