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

package io.spring.initializr.generator.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import io.spring.initializr.metadata.BillOfMaterials;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.DependencyGroup;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Kotlin;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.InitializrConfiguration.Platform;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.Repository;
import io.spring.initializr.metadata.Type;
import org.jspecify.annotations.Nullable;

import org.springframework.util.StringUtils;

/**
 * Easily create a {@link InitializrMetadata} instance for testing purposes.
 *
 * @author Stephane Nicoll
 */
public class InitializrMetadataTestBuilder {

	private final InitializrMetadataBuilder builder = InitializrMetadataBuilder.create();

	/**
	 * Create a builder with all defaults.
	 * @return a new builder
	 */
	public static InitializrMetadataTestBuilder withDefaults() {
		return new InitializrMetadataTestBuilder().addAllDefaults();
	}

	/**
	 * Create a builder with basic defaults.
	 * @return a new builder
	 */
	public static InitializrMetadataTestBuilder withBasicDefaults() {
		return new InitializrMetadataTestBuilder().addBasicDefaults();
	}

	/**
	 * Build the {@link InitializrMetadata}.
	 * @return the metadata
	 */
	public InitializrMetadata build() {
		return this.builder.build();
	}

	/**
	 * Add a dependency group with dependencies of the given IDs.
	 * @param name the name of the group
	 * @param ids the IDs of the dependencies
	 * @return this for method chaining
	 */
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

	/**
	 * Add a dependency group with the given dependencies.
	 * @param name the name of the group
	 * @param dependencies the dependencies
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDependencyGroup(String name, Dependency... dependencies) {
		this.builder.withCustomizer((it) -> {
			DependencyGroup group = new DependencyGroup();
			group.setName(name);
			group.getContent().addAll(Arrays.asList(dependencies));
			it.getDependencies().getContent().add(group);
		});
		return this;
	}

	/**
	 * Add basic defaults, as well as Gradle and Kotlin settings.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addAllDefaults() {
		return addBasicDefaults().setGradleEnv("1.0.6.RELEASE").setKotlinEnv("1.1.1");
	}

	/**
	 * Add default types, packagings, Java versions, languages, boot versions and
	 * configuration file formats.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addBasicDefaults() {
		return addDefaultTypes().addDefaultPackagings()
			.addDefaultJavaVersions()
			.addDefaultLanguages()
			.addDefaultConfigurationFileFormats()
			.addDefaultBootVersions();
	}

	/**
	 * Add the default Maven and Gradle types.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDefaultTypes() {
		return addType("maven-build", false, "/pom.xml", "maven", null, "build")
			.addType("maven-project", true, "/starter.zip", "maven", null, "project")
			.addType("gradle-build", false, "/build.gradle", "gradle", null, "build")
			.addType("gradle-project", false, "/starter.zip", "gradle", null, "project");
	}

	/**
	 * Add a type.
	 * @param id the ID
	 * @param defaultValue whether this is the default type
	 * @param action the action
	 * @param build the build system
	 * @param dialect the dialect of the build system
	 * @param format the format
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addType(String id, boolean defaultValue, @Nullable String action,
			@Nullable String build, @Nullable String dialect, @Nullable String format) {
		Type type = new Type();
		type.setId(id);
		type.setName(id);
		type.setDefault(defaultValue);
		type.setAction(action);
		if (StringUtils.hasText(build)) {
			type.getTags().put("build", build);
		}
		if (StringUtils.hasText(dialect)) {
			type.getTags().put("dialect", dialect);
		}
		if (StringUtils.hasText(format)) {
			type.getTags().put("format", format);
		}
		return addType(type);
	}

	/**
	 * Add a type.
	 * @param type the type
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addType(Type type) {
		this.builder.withCustomizer((it) -> it.getTypes().getContent().add(type));
		return this;
	}

	/**
	 * Add the {@code jar} and {@code war} packagings.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDefaultPackagings() {
		return addPackaging("jar", true).addPackaging("war", false);
	}

	/**
	 * Add a packaging.
	 * @param id the ID
	 * @param defaultValue whether this is the default packaging
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addPackaging(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement packaging = new DefaultMetadataElement();
			packaging.setId(id);
			packaging.setName(id);
			packaging.setDefault(defaultValue);
			it.getPackagings().addContent(packaging);
		});
		return this;
	}

	/**
	 * Add the default Java versions.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDefaultJavaVersions() {
		return addJavaVersion("1.6", false).addJavaVersion("1.7", false).addJavaVersion("1.8", true);
	}

	/**
	 * Add a Java version.
	 * @param version the version
	 * @param defaultValue whether this is the default Java version
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addJavaVersion(String version, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(version);
			element.setName(version);
			element.setDefault(defaultValue);
			it.getJavaVersions().addContent(element);
		});
		return this;
	}

	/**
	 * Add the {@code java}, {@code groovy} and {@code kotlin} languages.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDefaultLanguages() {
		return addLanguage("java", true).addLanguage("groovy", false).addLanguage("kotlin", false);
	}

	/**
	 * Add a language.
	 * @param id the ID
	 * @param defaultValue whether this is the default language
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addLanguage(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(id);
			element.setName(id);
			element.setDefault(defaultValue);
			it.getLanguages().addContent(element);
		});
		return this;
	}

	/**
	 * Add the {@code properties} and {@code yaml} configuration file formats.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDefaultConfigurationFileFormats() {
		return addConfigurationFileFormats("properties", true).addConfigurationFileFormats("yaml", false);
	}

	/**
	 * Add a configuration file format.
	 * @param id the ID
	 * @param defaultValue whether this is the default format
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addConfigurationFileFormats(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(id);
			element.setName(id);
			element.setDefault(defaultValue);
			it.getConfigurationFileFormats().addContent(element);
		});
		return this;
	}

	/**
	 * Add the default Spring Boot versions.
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addDefaultBootVersions() {
		return addBootVersion("2.2.17.RELEASE", false).addBootVersion("2.3.3.RELEASE", false)
			.addBootVersion("2.4.1", true)
			.addBootVersion("2.5.0-SNAPSHOT", false);
	}

	/**
	 * Add a Spring Boot version.
	 * @param id the version
	 * @param defaultValue whether this is the default version
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addBootVersion(String id, boolean defaultValue) {
		this.builder.withCustomizer((it) -> {
			DefaultMetadataElement element = new DefaultMetadataElement();
			element.setId(id);
			element.setName(id);
			element.setDefault(defaultValue);
			it.getBootVersions().addContent(element);
		});
		return this;
	}

	/**
	 * Add a bom.
	 * @param id the ID
	 * @param groupId the group ID
	 * @param artifactId the artifact ID
	 * @param version the version
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addBom(String id, String groupId, String artifactId, String version) {
		BillOfMaterials bom = BillOfMaterials.create(groupId, artifactId, version);
		return addBom(id, bom);
	}

	/**
	 * Add a bom.
	 * @param id the ID
	 * @param bom the bom
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addBom(String id, BillOfMaterials bom) {
		this.builder.withCustomizer((it) -> it.getConfiguration().getEnv().getBoms().put(id, bom));
		return this;
	}

	/**
	 * Set the compatibility range of the platform.
	 * @param platformCompatibilityRange the compatibility range
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder setPlatformCompatibilityRange(@Nullable String platformCompatibilityRange) {
		this.builder.withCustomizer(
				(it) -> it.getConfiguration().getEnv().getPlatform().setCompatibilityRange(platformCompatibilityRange));
		return this;
	}

	/**
	 * Set the compatibility ranges of the platform for each version format.
	 * @param v1Range the compatibility range for the {@code V1} version format
	 * @param v2Range the compatibility range for the {@code V2} version format
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder setPlatformVersionFormatCompatibilityRange(String v1Range, String v2Range) {
		this.builder.withCustomizer((it) -> {
			Platform platform = it.getConfiguration().getEnv().getPlatform();
			platform.setV1FormatCompatibilityRange(v1Range);
			platform.setV2FormatCompatibilityRange(v2Range);
		});
		return this;
	}

	/**
	 * Set the Gradle settings.
	 * @param dependencyManagementPluginVersion the version of the dependency management
	 * plugin
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder setGradleEnv(String dependencyManagementPluginVersion) {
		this.builder.withCustomizer((it) -> it.getConfiguration()
			.getEnv()
			.getGradle()
			.setDependencyManagementPluginVersion(dependencyManagementPluginVersion));
		return this;
	}

	/**
	 * Set the Kotlin settings.
	 * @param defaultKotlinVersion the default Kotlin version
	 * @param mappings the Kotlin version mappings
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder setKotlinEnv(String defaultKotlinVersion, Kotlin.Mapping... mappings) {
		this.builder.withCustomizer((it) -> {
			it.getConfiguration().getEnv().getKotlin().setDefaultVersion(defaultKotlinVersion);
			for (Kotlin.Mapping mapping : mappings) {
				it.getConfiguration().getEnv().getKotlin().getMappings().add(mapping);
			}
		});
		return this;
	}

	/**
	 * Set the Maven parent.
	 * @param groupId the group ID
	 * @param artifactId the artifact ID
	 * @param version the version
	 * @param relativePath the relative path
	 * @param includeSpringBootBom whether to include the Spring Boot bom
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder setMavenParent(String groupId, String artifactId, String version,
			@Nullable String relativePath, boolean includeSpringBootBom) {
		this.builder.withCustomizer((it) -> {
			ParentPom parent = it.getConfiguration().getEnv().getMaven().getParent();
			parent.setGroupId(groupId);
			parent.setArtifactId(artifactId);
			parent.setVersion(version);
			parent.setRelativePath(relativePath);
			parent.setIncludeSpringBootBom(includeSpringBootBom);
		});
		return this;
	}

	/**
	 * Add a repository with releases enabled.
	 * @param id the ID
	 * @param name the name
	 * @param url the URL
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addReleasesRepository(String id, String name, String url) {
		return addRepository(id, name, url, true, false);
	}

	/**
	 * Add a repository with snapshots enabled.
	 * @param id the ID
	 * @param name the name
	 * @param url the URL
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addSnapshotsRepository(String id, String name, String url) {
		return addRepository(id, name, url, false, true);
	}

	/**
	 * Add a repository.
	 * @param id the ID
	 * @param name the name
	 * @param url the URL
	 * @param releasesEnabled whether releases are enabled
	 * @param snapshotsEnabled whether snapshots are enabled
	 * @return this for method chaining
	 */
	public InitializrMetadataTestBuilder addRepository(String id, String name, String url, boolean releasesEnabled,
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
			repo.setReleasesEnabled(releasesEnabled);
			repo.setSnapshotsEnabled(snapshotsEnabled);
			it.getConfiguration().getEnv().getRepositories().put(id, repo);
		});
		return this;
	}

}
