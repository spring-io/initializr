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

package io.spring.initializr.web.project;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import org.springframework.util.StringUtils;

/**
 * The base settings of a project request. Only these can be bound by user's input.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequest {

	private List<String> dependencies = new ArrayList<>();

	private @Nullable String name;

	private @Nullable String type;

	private @Nullable String description;

	private @Nullable String groupId;

	private @Nullable String artifactId;

	private @Nullable String version;

	private @Nullable String bootVersion;

	private @Nullable String packaging;

	private @Nullable String applicationName;

	private @Nullable String language;

	private @Nullable String configurationFileFormat;

	private @Nullable String packageName;

	private @Nullable String javaVersion;

	// The base directory to create in the archive - no baseDir by default
	private @Nullable String baseDir;

	public List<String> getDependencies() {
		return this.dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public @Nullable String getName() {
		return this.name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public @Nullable String getType() {
		return this.type;
	}

	public void setType(@Nullable String type) {
		this.type = type;
	}

	public @Nullable String getDescription() {
		return this.description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	public @Nullable String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(@Nullable String groupId) {
		this.groupId = groupId;
	}

	public @Nullable String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(@Nullable String artifactId) {
		this.artifactId = artifactId;
	}

	public @Nullable String getVersion() {
		return this.version;
	}

	public void setVersion(@Nullable String version) {
		this.version = version;
	}

	public @Nullable String getBootVersion() {
		return this.bootVersion;
	}

	public void setBootVersion(@Nullable String bootVersion) {
		this.bootVersion = bootVersion;
	}

	public @Nullable String getPackaging() {
		return this.packaging;
	}

	public void setPackaging(@Nullable String packaging) {
		this.packaging = packaging;
	}

	public @Nullable String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(@Nullable String applicationName) {
		this.applicationName = applicationName;
	}

	public @Nullable String getLanguage() {
		return this.language;
	}

	public void setLanguage(@Nullable String language) {
		this.language = language;
	}

	public @Nullable String getConfigurationFileFormat() {
		return this.configurationFileFormat;
	}

	public void setConfigurationFileFormat(@Nullable String configurationFileFormat) {
		this.configurationFileFormat = configurationFileFormat;
	}

	public @Nullable String getPackageName() {
		if (StringUtils.hasText(this.packageName)) {
			return this.packageName;
		}
		if (StringUtils.hasText(this.groupId) && StringUtils.hasText(this.artifactId)) {
			return getGroupId() + "." + getArtifactId();
		}
		return null;
	}

	public void setPackageName(@Nullable String packageName) {
		this.packageName = packageName;
	}

	public @Nullable String getJavaVersion() {
		return this.javaVersion;
	}

	public void setJavaVersion(@Nullable String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public @Nullable String getBaseDir() {
		return this.baseDir;
	}

	public void setBaseDir(@Nullable String baseDir) {
		this.baseDir = baseDir;
	}

}
