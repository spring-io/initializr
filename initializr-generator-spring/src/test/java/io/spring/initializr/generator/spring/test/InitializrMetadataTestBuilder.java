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

package io.spring.initializr.generator.spring.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.Repository;
import io.spring.initializr.metadata.Type;

import org.springframework.util.StringUtils;

/**
 * Easily create a {@link InitializrMetadata} instance for testing purposes.
 *
 * @author Stephane Nicoll
 */
public class InitializrMetadataTestBuilder {

	private final InitializrMetadataBuilder builder = InitializrMetadataBuilder.create();

	public static InitializrMetadataTestBuilder withDefaults() {
		return new InitializrMetadataTestBuilder().addAllDefaults();
	}

	public static InitializrMetadataTestBuilder withBasicDefaults() {
		return new InitializrMetadataTestBuilder().addBasicDefaults();
	}

	public InitializrMetadata build() {
		return this.builder.build();
	}

	public InitializrMetadataTestBuilder addDependencyGroup(String name, String... ids) {
		this.builder.withCustomizer((it) -> {
			DependencyGroup group = new DependencyGroup();
			group.setName(name);
			for (String id : ids) {
				Dependency dependency = new Dependency();
				dependency.setId(id);
				group.getContent().add(dependency);
			}
			it.getDependencies().getContent().add(group);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addDependencyGroup(String name,
			Dependency... dependencies) {
		this.builder.withCustomizer((it) -> {
			DependencyGroup group = new DependencyGroup();
			group.setName(name);
			group.getContent().addAll(Arrays.asList(dependencies));
			it.getDependencies().getContent().add(group);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addAllDefaults() {
		return addBasicDefaults().setGradleEnv("1.0.6.RELEASE").setKotlinEnv("1.1.1");
	}

	public InitializrMetadataTestBuilder addBasicDefaults() {
		return addDefaultTypes().addDefaultPackagings().addDefaultJavaVersions()
				.addDefaultLanguages().addDefaultBootVersions();
	}

	public InitializrMetadataTestBuilder addDefaultTypes() {
		return addType("maven-build", false, "/pom.xml", "maven", "build")
				.addType("maven-project", true, "/starter.zip", "maven", "project")
				.addType("gradle-build", false, "/build.gradle", "gradle", "build")
				.addType("gradle-project", false, "/starter.zip", "gradle", "project");
	}

	public InitializrMetadataTestBuilder addType(String id, boolean defaultValue,
			String action, String build, String format) {
		Type type = new Type();
		type.setId(id);
		type.setName(id);
		type.setDefault(defaultValue);
		type.setAction(action);
		if (StringUtils.hasText(build)) {
			type.getTags().put("build", build);
		}
		if (StringUtils.hasText(format)) {
			type.getTags().put("format", format);
		}
		return addType(type);
	}

	public InitializrMetadataTestBuilder addType(Type type) {
		this.builder.withCustomizer((it) -> it.getTypes().getContent().add(type));
		return this;
	}

	public InitializrMetadataTestBuilder addDefaultPackagings() {
		return addPackaging("jar", true).addPackaging("war", false);
	}

	public InitializrMetadataTestBuilder addPackaging(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement packaging = new DefaultMetadataElement();
			packaging.setId(id);
			packaging.setName(id);
			packaging.setDefault(defaultValue);
			it.getPackagings().getContent().add(packaging);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addDefaultJavaVersions() {
		return addJavaVersion("1.6", false).addJavaVersion("1.7", false)
				.addJavaVersion("1.8", true);
	}

	public InitializrMetadataTestBuilder addJavaVersion(String version,
			boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(version);
			element.setName(version);
			element.setDefault(defaultValue);
			it.getJavaVersions().getContent().add(element);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addDefaultLanguages() {
		return addLanguage("java", true).addLanguage("groovy", false)
				.addLanguage("kotlin", false);
	}

	public InitializrMetadataTestBuilder addLanguage(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(id);
			element.setName(id);
			element.setDefault(defaultValue);
			it.getLanguages().getContent().add(element);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addDefaultBootVersions() {
		return addBootVersion("1.5.17.RELEASE", false)
				.addBootVersion("2.0.3.RELEASE", false)
				.addBootVersion("2.1.1.RELEASE", true)
				.addBootVersion("2.2.0.BUILD-SNAPSHOT", false);
	}

	public InitializrMetadataTestBuilder addBootVersion(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(id);
			element.setName(id);
			element.setDefault(defaultValue);
			it.getBootVersions().getContent().add(element);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addBom(String id, String groupId,
			String artifactId, String version) {
		BillOfMaterials bom = BillOfMaterials.create(groupId, artifactId, version);
		return addBom(id, bom);
	}

	public InitializrMetadataTestBuilder addBom(String id, BillOfMaterials bom) {
		this.builder.withCustomizer(
				(it) -> it.getConfiguration().getEnv().getBoms().put(id, bom));
		return this;
	}

	public InitializrMetadataTestBuilder setGradleEnv(
			String dependencyManagementPluginVersion) {
		this.builder.withCustomizer((it) -> it.getConfiguration().getEnv().getGradle()
				.setDependencyManagementPluginVersion(dependencyManagementPluginVersion));
		return this;
	}

	public InitializrMetadataTestBuilder setKotlinEnv(String defaultKotlinVersion,
			Kotlin.Mapping... mappings) {
		this.builder.withCustomizer((it) -> {
			it.getConfiguration().getEnv().getKotlin()
					.setDefaultVersion(defaultKotlinVersion);
			for (Kotlin.Mapping mapping : mappings) {
				it.getConfiguration().getEnv().getKotlin().getMappings().add(mapping);
			}
		});
		return this;
	}

	public InitializrMetadataTestBuilder setMavenParent(String groupId, String artifactId,
			String version, boolean includeSpringBootBom) {
		this.builder.withCustomizer((it) -> {
			ParentPom parent = it.getConfiguration().getEnv().getMaven().getParent();
			parent.setGroupId(groupId);
			parent.setArtifactId(artifactId);
			parent.setVersion(version);
			parent.setIncludeSpringBootBom(includeSpringBootBom);
		});
		return this;
	}

	public InitializrMetadataTestBuilder addRepository(String id, String name, String url,
			boolean snapshotsEnabled) {
		this.builder.withCustomizer((it) -> {
			Repository repo = new Repository();
			repo.setName(name);
			try {
				repo.setUrl(new URL(url));
			}
			catch (MalformedURLException ex) {
				throw new IllegalArgumentException("Cannot create URL", ex);
			}
			repo.setSnapshotsEnabled(snapshotsEnabled);
			it.getConfiguration().getEnv().getRepositories().put(id, repo);
		});
		return this;
	}

}
