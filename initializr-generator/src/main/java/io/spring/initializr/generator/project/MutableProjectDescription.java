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

package io.spring.initializr.generator.project;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.version.Version;

import org.springframework.util.StringUtils;

/**
 * A mutable implementation of {@link ProjectDescription}.
 *
 * @author Andy Wilkinson
 */
public class MutableProjectDescription implements ProjectDescription {

	private Version platformVersion;

	private BuildSystem buildSystem;

	private Packaging packaging;

	private Language language;

	private final Map<String, Dependency> requestedDependencies = new LinkedHashMap<>();

	private String groupId;

	private String artifactId;

	private String version;

	private String name;

	private String description;

	private String applicationName;

	private String packageName;

	private String baseDirectory;

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
	public Version getPlatformVersion() {
		return this.platformVersion;
	}

	public void setPlatformVersion(Version platformVersion) {
		this.platformVersion = platformVersion;
	}

	@Override
	public BuildSystem getBuildSystem() {
		return this.buildSystem;
	}

	public void setBuildSystem(BuildSystem buildSystem) {
		this.buildSystem = buildSystem;
	}

	@Override
	public Packaging getPackaging() {
		return this.packaging;
	}

	public void setPackaging(Packaging packaging) {
		this.packaging = packaging;
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Dependency addDependency(String id, Dependency dependency) {
		return this.requestedDependencies.put(id, dependency);
	}

	public Dependency addDependency(String id, Dependency.Builder<?> builder) {
		return addDependency(id, builder.build());
	}

	public Dependency removeDependency(String id) {
		return this.requestedDependencies.remove(id);
	}

	@Override
	public Map<String, Dependency> getRequestedDependencies() {
		return Collections.unmodifiableMap(this.requestedDependencies);
	}

	@Override
	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getApplicationName() {
		return this.applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@Override
	public String getPackageName() {
		if (StringUtils.hasText(this.packageName)) {
			return this.packageName;
		}
		if (StringUtils.hasText(this.groupId) && StringUtils.hasText(this.artifactId)) {
			return this.groupId + "." + this.artifactId;
		}
		return null;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String getBaseDirectory() {
		return this.baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

}
