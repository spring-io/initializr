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

package io.spring.initializr.metadata;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.SourceVersion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.spring.initializr.generator.version.InvalidVersionException;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.Version.Format;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.StringUtils;

/**
 * Various configuration options used by the service.
 *
 * @author Stephane Nicoll
 * @author Chris Bono
 */
public class InitializrConfiguration {

	/**
	 * Environment options.
	 */
	@NestedConfigurationProperty
	private final Env env = new Env();

	public Env getEnv() {
		return this.env;
	}

	public void validate() {
		this.env.validate();
	}

	public void merge(InitializrConfiguration other) {
		this.env.merge(other.env);
	}

	/**
	 * Generate a suitable application name based on the specified name. If no suitable
	 * application name can be generated from the specified {@code name}, the
	 * {@link Env#getFallbackApplicationName()} is used instead.
	 * <p>
	 * No suitable application name can be generated if the name is {@code null} or if it
	 * contains an invalid character for a class identifier.
	 * @param name the the source name
	 * @return the generated application name
	 * @see Env#getFallbackApplicationName()
	 * @see Env#getInvalidApplicationNames()
	 */
	public String generateApplicationName(String name) {
		if (!StringUtils.hasText(name)) {
			return this.env.fallbackApplicationName;
		}
		String text = splitCamelCase(name.trim());
		// TODO: fix this
		String result = unsplitWords(text);
		if (!result.endsWith("Application")) {
			result = result + "Application";
		}
		String candidate = StringUtils.capitalize(result);
		if (hasInvalidChar(candidate) || this.env.invalidApplicationNames.contains(candidate)) {
			return this.env.fallbackApplicationName;
		}
		else {
			return candidate;
		}
	}

	/**
	 * Clean the specified package name if necessary. If the package name cannot be
	 * transformed to a valid package name, the {@code defaultPackageName} is used
	 * instead.
	 * <p>
	 * The package name cannot be cleaned if the specified {@code packageName} is
	 * {@code null} or if it contains an invalid character for a class identifier.
	 * @param packageName the package name
	 * @param defaultPackageName the default package name
	 * @return the cleaned package name
	 * @see Env#getInvalidPackageNames()
	 */
	public String cleanPackageName(String packageName, String defaultPackageName) {
		if (!StringUtils.hasText(packageName)) {
			return defaultPackageName;
		}
		String candidate = cleanPackageName(packageName);
		if (!StringUtils.hasText(candidate)) {
			return defaultPackageName;
		}
		if (hasInvalidChar(candidate.replace(".", "")) || this.env.invalidPackageNames.contains(candidate)) {
			return defaultPackageName;
		}
		if (hasReservedKeyword(candidate)) {
			return defaultPackageName;
		}
		else {
			return candidate;
		}
	}

	static String cleanPackageName(String packageName) {
		String[] elements = packageName.trim().replaceAll("-", "").split("\\W+");
		StringBuilder sb = new StringBuilder();
		for (String element : elements) {
			element = element.replaceFirst("^[0-9]+(?!$)", "");
			if (!element.matches("[0-9]+") && sb.length() > 0) {
				sb.append(".");
			}
			sb.append(element);
		}
		return sb.toString();
	}

	private static String unsplitWords(String text) {
		return String.join("",
				Arrays.stream(text.split("(_|-| |:)+")).map(StringUtils::capitalize).toArray(String[]::new));
	}

	private static String splitCamelCase(String text) {
		return String.join("", Arrays.stream(text.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"))
				.map((it) -> StringUtils.capitalize(it.toLowerCase())).toArray(String[]::new));
	}

	private static boolean hasInvalidChar(String text) {
		if (!Character.isJavaIdentifierStart(text.charAt(0))) {
			return true;
		}
		if (text.length() > 1) {
			for (int i = 1; i < text.length(); i++) {
				if (!Character.isJavaIdentifierPart(text.charAt(i))) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean hasReservedKeyword(final String packageName) {
		return Arrays.stream(packageName.split("\\.")).anyMatch(SourceVersion::isKeyword);
	}

	/**
	 * Defines additional environment settings.
	 */
	public static class Env {

		/**
		 * The url of the repository servicing distribution bundle.
		 */
		private String artifactRepository = "https://repo.spring.io/release/";

		/**
		 * The metadata url of the Spring Boot project.
		 */
		private String springBootMetadataUrl = "https://spring.io/project_metadata/spring-boot";

		/**
		 * Tracking code for Google Analytics. Only enabled if a value is explicitly
		 * provided.
		 */
		private String googleAnalyticsTrackingCode;

		/**
		 * The application name to use if none could be generated.
		 */
		private String fallbackApplicationName = "Application";

		/**
		 * The list of invalid application names. If such name is chosen or generated, the
		 * "fallbackApplicationName" should be used instead.
		 */
		private List<String> invalidApplicationNames = new ArrayList<>(
				Arrays.asList("SpringApplication", "SpringBootApplication"));

		/**
		 * The list of invalid package names. If such name is chosen or generated, the the
		 * default package name should be used instead.
		 */
		private List<String> invalidPackageNames = new ArrayList<>(Collections.singletonList("org.springframework"));

		/**
		 * Force SSL support. When enabled, any access using http generate https links and
		 * browsers are redirected to https for html content.
		 */
		private boolean forceSsl;

		/**
		 * The "BillOfMaterials" that are referenced in this instance, identified by an
		 * arbitrary identifier that can be used in the dependencies definition.
		 */
		private final Map<String, BillOfMaterials> boms = new LinkedHashMap<>();

		/**
		 * The "Repository" instances that are referenced in this instance, identified by
		 * an arbitrary identifier that can be used in the dependencies definition.
		 */
		private final Map<String, Repository> repositories = new LinkedHashMap<>();

		/**
		 * Gradle-specific settings.
		 */
		@NestedConfigurationProperty
		private final Gradle gradle = new Gradle();

		/**
		 * Kotlin-specific settings.
		 */
		@NestedConfigurationProperty
		private final Kotlin kotlin = new Kotlin();

		/**
		 * Maven-specific settings.
		 */
		@NestedConfigurationProperty
		private final Maven maven = new Maven();

		/**
		 * Platform-specific settings.
		 */
		@NestedConfigurationProperty
		private final Platform platform = new Platform();

		public Env() {
			try {
				this.repositories.put("spring-snapshots",
						new Repository("Spring Snapshots", new URL("https://repo.spring.io/snapshot"), true));
				this.repositories.put("spring-milestones",
						new Repository("Spring Milestones", new URL("https://repo.spring.io/milestone"), false));
			}
			catch (MalformedURLException ex) {
				throw new IllegalStateException("Cannot parse URL", ex);
			}
		}

		public String getSpringBootMetadataUrl() {
			return this.springBootMetadataUrl;
		}

		public void setSpringBootMetadataUrl(String springBootMetadataUrl) {
			this.springBootMetadataUrl = springBootMetadataUrl;
		}

		public String getGoogleAnalyticsTrackingCode() {
			return this.googleAnalyticsTrackingCode;
		}

		public void setGoogleAnalyticsTrackingCode(String googleAnalyticsTrackingCode) {
			this.googleAnalyticsTrackingCode = googleAnalyticsTrackingCode;
		}

		public String getFallbackApplicationName() {
			return this.fallbackApplicationName;
		}

		public void setFallbackApplicationName(String fallbackApplicationName) {
			this.fallbackApplicationName = fallbackApplicationName;
		}

		public List<String> getInvalidApplicationNames() {
			return this.invalidApplicationNames;
		}

		public void setInvalidApplicationNames(List<String> invalidApplicationNames) {
			this.invalidApplicationNames = invalidApplicationNames;
		}

		public List<String> getInvalidPackageNames() {
			return this.invalidPackageNames;
		}

		public void setInvalidPackageNames(List<String> invalidPackageNames) {
			this.invalidPackageNames = invalidPackageNames;
		}

		public boolean isForceSsl() {
			return this.forceSsl;
		}

		public void setForceSsl(boolean forceSsl) {
			this.forceSsl = forceSsl;
		}

		public String getArtifactRepository() {
			return this.artifactRepository;
		}

		public Map<String, BillOfMaterials> getBoms() {
			return this.boms;
		}

		public Map<String, Repository> getRepositories() {
			return this.repositories;
		}

		public Gradle getGradle() {
			return this.gradle;
		}

		public Kotlin getKotlin() {
			return this.kotlin;
		}

		public Maven getMaven() {
			return this.maven;
		}

		public Platform getPlatform() {
			return this.platform;
		}

		public void setArtifactRepository(String artifactRepository) {
			if (!artifactRepository.endsWith("/")) {
				artifactRepository = artifactRepository + "/";
			}
			this.artifactRepository = artifactRepository;
		}

		public void validate() {
			this.maven.parent.validate();
			this.boms.forEach((k, v) -> v.validate());
			this.kotlin.validate();
			updateCompatibilityRange(VersionParser.DEFAULT);
		}

		public void updateCompatibilityRange(VersionParser versionParser) {
			this.getBoms().values().forEach((it) -> it.updateCompatibilityRange(versionParser));
			this.getKotlin().updateCompatibilityRange(versionParser);
			this.getPlatform().updateCompatibilityRange(versionParser);
		}

		public void merge(Env other) {
			this.artifactRepository = other.artifactRepository;
			this.springBootMetadataUrl = other.springBootMetadataUrl;
			this.googleAnalyticsTrackingCode = other.googleAnalyticsTrackingCode;
			this.fallbackApplicationName = other.fallbackApplicationName;
			this.invalidApplicationNames = other.invalidApplicationNames;
			this.forceSsl = other.forceSsl;
			this.gradle.merge(other.gradle);
			this.kotlin.merge(other.kotlin);
			this.maven.merge(other.maven);
			this.platform.merge(other.platform);
			other.boms.forEach(this.boms::putIfAbsent);
			other.repositories.forEach(this.repositories::putIfAbsent);
		}

		/**
		 * Gradle details.
		 */
		public static class Gradle {

			/**
			 * Version of the "dependency-management-plugin" to use.
			 */
			private String dependencyManagementPluginVersion = "1.0.0.RELEASE";

			private void merge(Gradle other) {
				this.dependencyManagementPluginVersion = other.dependencyManagementPluginVersion;
			}

			public String getDependencyManagementPluginVersion() {
				return this.dependencyManagementPluginVersion;
			}

			public void setDependencyManagementPluginVersion(String dependencyManagementPluginVersion) {
				this.dependencyManagementPluginVersion = dependencyManagementPluginVersion;
			}

		}

		/**
		 * Kotlin details.
		 */
		public static class Kotlin {

			/**
			 * Default kotlin version.
			 */
			private String defaultVersion;

			/**
			 * Kotlin version mappings.
			 */
			private final List<Mapping> mappings = new ArrayList<>();

			/**
			 * Resolve the kotlin version to use based on the Spring Boot version.
			 * @param bootVersion the Spring Boot version
			 * @return the kotlin version to use
			 */
			public String resolveKotlinVersion(Version bootVersion) {
				for (Mapping mapping : this.mappings) {
					if (mapping.range.match(bootVersion)) {
						return mapping.version;
					}
				}
				if (this.defaultVersion == null) {
					throw new InvalidInitializrMetadataException(
							"No Kotlin version mapping available for " + bootVersion);
				}
				return this.defaultVersion;
			}

			public String getDefaultVersion() {
				return this.defaultVersion;
			}

			public void setDefaultVersion(String defaultVersion) {
				this.defaultVersion = defaultVersion;
			}

			public List<Mapping> getMappings() {
				return this.mappings;
			}

			public void validate() {
				this.mappings.forEach((m) -> {
					if (m.compatibilityRange == null) {
						throw new InvalidInitializrMetadataException(
								"CompatibilityRange is mandatory, invalid version mapping for " + this);
					}
					if (m.version == null) {
						throw new InvalidInitializrMetadataException(
								"Version is mandatory, invalid version mapping for " + this);
					}
				});
				updateCompatibilityRange(VersionParser.DEFAULT);
			}

			public void updateCompatibilityRange(VersionParser versionParser) {
				this.mappings.forEach((it) -> {
					try {
						it.range = versionParser.parseRange(it.compatibilityRange);
					}
					catch (InvalidVersionException ex) {
						throw new InvalidInitializrMetadataException(
								"Invalid compatibility range " + it.compatibilityRange + " for " + this, ex);
					}
				});
			}

			private void merge(Kotlin other) {
				this.defaultVersion = other.defaultVersion;
				this.mappings.clear();
				this.mappings.addAll(other.mappings);
			}

			/**
			 * Map several attribute of the dependency for a given compatibility range.
			 */
			public static class Mapping {

				/**
				 * The compatibility range of this mapping.
				 */
				private String compatibilityRange;

				/**
				 * The kotlin version for this mapping.
				 */
				private String version;

				@JsonIgnore
				private VersionRange range;

				public String getCompatibilityRange() {
					return this.compatibilityRange;
				}

				public void setCompatibilityRange(String compatibilityRange) {
					this.compatibilityRange = compatibilityRange;
				}

				public String getVersion() {
					return this.version;
				}

				public void setVersion(String version) {
					this.version = version;
				}

			}

		}

		/**
		 * Maven details.
		 */
		public static class Maven {

			private static final String DEFAULT_PARENT_GROUP_ID = "org.springframework.boot";

			private static final String DEFAULT_PARENT_ARTIFACT_ID = "spring-boot-starter-parent";

			/**
			 * Custom parent pom to use for generated projects.
			 */
			private final ParentPom parent = new ParentPom();

			public ParentPom getParent() {
				return this.parent;
			}

			private void merge(Maven other) {
				this.parent.groupId = other.parent.groupId;
				this.parent.artifactId = other.parent.artifactId;
				this.parent.version = other.parent.version;
				this.parent.includeSpringBootBom = other.parent.includeSpringBootBom;
			}

			/**
			 * Resolve the parent pom to use. If no custom parent pom is set, the standard
			 * spring boot parent pom with the specified {@code bootVersion} is used.
			 * @param bootVersion the Spring Boot version
			 * @return the parent POM
			 */
			public ParentPom resolveParentPom(String bootVersion) {
				return (StringUtils.hasText(this.parent.groupId) ? this.parent
						: new ParentPom(DEFAULT_PARENT_GROUP_ID, DEFAULT_PARENT_ARTIFACT_ID, bootVersion));
			}

			/**
			 * Check if the specified {@link ParentPom} is the default spring boot starter
			 * parent.
			 * @param parentPom the parent pom to check
			 * @return {@code true} if the {@code parentPom} is the spring boot starter
			 * parent
			 */
			public boolean isSpringBootStarterParent(ParentPom parentPom) {
				return DEFAULT_PARENT_GROUP_ID.equals(parentPom.getGroupId())
						&& DEFAULT_PARENT_ARTIFACT_ID.equals(parentPom.getArtifactId());
			}

			/**
			 * Parent POM details.
			 */
			public static class ParentPom {

				/**
				 * Parent pom groupId.
				 */
				private String groupId;

				/**
				 * Parent pom artifactId.
				 */
				private String artifactId;

				/**
				 * Parent pom version.
				 */
				private String version;

				/**
				 * Add the "spring-boot-dependencies" BOM to the project.
				 */
				private boolean includeSpringBootBom;

				public ParentPom(String groupId, String artifactId, String version) {
					this.groupId = groupId;
					this.artifactId = artifactId;
					this.version = version;
				}

				public ParentPom() {
				}

				public String getGroupId() {
					return this.groupId;
				}

				public void setGroupId(String groupId) {
					this.groupId = groupId;
				}

				public String getArtifactId() {
					return this.artifactId;
				}

				public void setArtifactId(String artifactId) {
					this.artifactId = artifactId;
				}

				public String getVersion() {
					return this.version;
				}

				public void setVersion(String version) {
					this.version = version;
				}

				public boolean isIncludeSpringBootBom() {
					return this.includeSpringBootBom;
				}

				public void setIncludeSpringBootBom(boolean includeSpringBootBom) {
					this.includeSpringBootBom = includeSpringBootBom;
				}

				public void validate() {
					if (!((!StringUtils.hasText(this.groupId) && !StringUtils.hasText(this.artifactId)
							&& !StringUtils.hasText(this.version))
							|| (StringUtils.hasText(this.groupId) && StringUtils.hasText(this.artifactId)
									&& StringUtils.hasText(this.version)))) {
						throw new InvalidInitializrMetadataException(
								"Custom maven pom " + "requires groupId, artifactId and version");
					}
				}

			}

		}

	}

	/**
	 * Platform-specific settings.
	 */
	public static class Platform {

		/**
		 * Compatibility range of supported platform versions. Requesting metadata or
		 * project generation with a platform version that does not match this range is
		 * not supported.
		 */
		private String compatibilityRange;

		@JsonIgnore
		private VersionRange range;

		/**
		 * Compatibility range of platform versions using the first version format.
		 */
		private String v1FormatCompatibilityRange;

		@JsonIgnore
		private VersionRange v1FormatRange;

		/**
		 * Compatibility range of platform versions using the second version format.
		 */
		private String v2FormatCompatibilityRange;

		@JsonIgnore
		private VersionRange v2FormatRange;

		public void updateCompatibilityRange(VersionParser versionParser) {
			this.range = (this.compatibilityRange != null) ? versionParser.parseRange(this.compatibilityRange) : null;
			this.v1FormatRange = (this.v1FormatCompatibilityRange != null)
					? versionParser.parseRange(this.v1FormatCompatibilityRange) : null;
			this.v2FormatRange = (this.v2FormatCompatibilityRange != null)
					? versionParser.parseRange(this.v2FormatCompatibilityRange) : null;
		}

		private void merge(Platform other) {
			this.compatibilityRange = other.compatibilityRange;
			this.range = other.range;
			this.v1FormatCompatibilityRange = other.v1FormatCompatibilityRange;
			this.v1FormatRange = other.v1FormatRange;
			this.v2FormatCompatibilityRange = other.v2FormatCompatibilityRange;
			this.v2FormatRange = other.v2FormatRange;
		}

		/**
		 * Specify whether the specified {@linkplain Version platform version} is
		 * supported.
		 * @param platformVersion the platform version to check
		 * @return {@code true} if this version is supported, {@code false} otherwise
		 */
		public boolean isCompatibleVersion(Version platformVersion) {
			return (this.range == null || this.range.match(platformVersion));
		}

		public String determineCompatibilityRangeRequirement() {
			return this.range.toString();
		}

		/**
		 * Format the expected {@link Version platform version}.
		 * @param platformVersion a platform version
		 * @return a platform version in the suitable format
		 */
		public Version formatPlatformVersion(Version platformVersion) {
			Format format = getExpectedVersionFormat(platformVersion);
			return platformVersion.format(format);
		}

		private Format getExpectedVersionFormat(Version version) {
			if (this.v2FormatRange != null && this.v2FormatRange.match(version)) {
				return Format.V2;
			}
			if (this.v1FormatRange != null && this.v1FormatRange.match(version)) {
				return Format.V1;
			}
			return version.getFormat();
		}

		public String getCompatibilityRange() {
			return this.compatibilityRange;
		}

		public void setCompatibilityRange(String compatibilityRange) {
			this.compatibilityRange = compatibilityRange;
		}

		public String getV1FormatCompatibilityRange() {
			return this.v1FormatCompatibilityRange;
		}

		public void setV1FormatCompatibilityRange(String v1FormatCompatibilityRange) {
			this.v1FormatCompatibilityRange = v1FormatCompatibilityRange;
		}

		public String getV2FormatCompatibilityRange() {
			return this.v2FormatCompatibilityRange;
		}

		public void setV2FormatCompatibilityRange(String v2FormatCompatibilityRange) {
			this.v2FormatCompatibilityRange = v2FormatCompatibilityRange;
		}

	}

}
