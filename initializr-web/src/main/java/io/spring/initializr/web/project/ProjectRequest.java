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

package io.spring.initializr.web.project;

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.generator.buildsystem.ProjectMetadata;

import org.springframework.util.StringUtils;

/**
 * The base settings of a project request. Only these can be bound by user's input.
 *
 * @author Stephane Nicoll
 */
public class ProjectRequest {

	private List<String> dependencies = new ArrayList<>();

	private String name;

	private String description;

	private String version;

	private String bootVersion;

	private String applicationName;

	// The base directory to create in the archive - no baseDir by default
	private String baseDir;

	private ProjectMetadata projectMetadata = new ProjectMetadata(null, null, null, null, null, null, null);

	public List<String> getDependencies() {
		return this.dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return this.projectMetadata.getType();
	}

	public void setType(String type) {
		this.projectMetadata.setType(type);
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupId() {
		return this.projectMetadata.getGroupId();
	}

	public void setGroupId(String groupId) {
		this.projectMetadata.setGroupId(groupId);
	}

	public String getArtifactId() {
		return this.projectMetadata.getArtifactId();
	}

	public void setArtifactId(String artifactId) {
		this.projectMetadata.setArtifactId(artifactId);
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getBootVersion() {
		return this.bootVersion;
	}

	public void setBootVersion(String bootVersion) {
		this.bootVersion = bootVersion;
	}

	public String getPackaging() {
		return this.projectMetadata.getPackaging();
	}

	public void setPackaging(String packaging) {
		this.projectMetadata.setPackaging(packaging);
	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getLanguage() {
		return this.projectMetadata.getLanguage();
	}

	public void setLanguage(String language) {
		this.projectMetadata.setLanguage(language);
	}

	public String getPackageName() {
		if (StringUtils.hasText(this.projectMetadata.getPackageName())) {
			return this.projectMetadata.getPackageName();
		}
		if (StringUtils.hasText(this.projectMetadata.getGroupId())
				&& StringUtils.hasText(this.projectMetadata.getArtifactId())) {
			return getGroupId() + "." + getArtifactId();
		}
		return null;
	}

	public void setPackageName(String packageName) {
		this.projectMetadata.setPackageName(packageName);
	}

	public String getJavaVersion() {
		return this.projectMetadata.getJavaVersion();
	}

	public void setJavaVersion(String javaVersion) {
		this.projectMetadata.setJavaVersion(javaVersion);
	}

	public String getBaseDir() {
		return this.baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

}
