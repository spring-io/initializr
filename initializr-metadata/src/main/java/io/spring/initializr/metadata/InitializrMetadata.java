/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.metadata;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.spring.initializr.generator.version.InvalidVersionException;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionProperty;
import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Meta-data used to generate a project.
 *
 * @author Stephane Nicoll
 * @see ServiceCapability
 */
public class InitializrMetadata {

	private final InitializrConfiguration configuration;

	private final DependenciesCapability dependencies = new DependenciesCapability();

	private final TypeCapability types = new TypeCapability();

	private final SingleSelectCapability bootVersions = new SingleSelectCapability("bootVersion", "Spring Boot Version",
			"spring boot version");

	private final SingleSelectCapability packagings = new SingleSelectCapability("packaging", "Packaging",
			"project packaging");

	private final SingleSelectCapability javaVersions = new SingleSelectCapability("javaVersion", "Java Version",
			"language level");

	private final SingleSelectCapability languages = new SingleSelectCapability("language", "Language",
			"programming language");

	private final SingleSelectCapability configurationFileFormats = new SingleSelectCapability(
			"configurationFileFormat", "Configuration File Format", "configuration file format");

	private final TextCapability name = new TextCapability("name", "Name", "project name (infer application name)");

	private final TextCapability description = new TextCapability("description", "Description", "project description");

	private final TextCapability groupId = new TextCapability("groupId", "Group", "project coordinates");

	private final TextCapability artifactId = new ArtifactIdCapability(this.name);

	private final TextCapability version = new TextCapability("version", "Version", "project version");

	private final TextCapability packageName = new PackageCapability(this.groupId, this.artifactId);

	public InitializrMetadata() {
		this(new InitializrConfiguration());
	}

	protected InitializrMetadata(InitializrConfiguration configuration) {
		this.configuration = configuration;
	}

	public InitializrConfiguration getConfiguration() {
		return this.configuration;
	}

	public DependenciesCapability getDependencies() {
		return this.dependencies;
	}

	public TypeCapability getTypes() {
		return this.types;
	}

	public SingleSelectCapability getBootVersions() {
		return this.bootVersions;
	}

	public SingleSelectCapability getPackagings() {
		return this.packagings;
	}

	public SingleSelectCapability getJavaVersions() {
		return this.javaVersions;
	}

	public SingleSelectCapability getLanguages() {
		return this.languages;
	}

	public SingleSelectCapability getConfigurationFileFormats() {
		return this.configurationFileFormats;
	}

	public TextCapability getName() {
		return this.name;
	}

	public TextCapability getDescription() {
		return this.description;
	}

	public TextCapability getGroupId() {
		return this.groupId;
	}

	public TextCapability getArtifactId() {
		return this.artifactId;
	}

	public TextCapability getVersion() {
		return this.version;
	}

	public TextCapability getPackageName() {
		return this.packageName;
	}

	/**
	 * Merge this instance with the specified argument.
	 * @param other the other instance
	 */
	public void merge(InitializrMetadata other) {
		this.configuration.merge(other.configuration);
		this.dependencies.merge(other.dependencies);
		this.types.merge(other.types);
		this.bootVersions.merge(other.bootVersions);
		this.packagings.merge(other.packagings);
		this.javaVersions.merge(other.javaVersions);
		this.languages.merge(other.languages);
		this.configurationFileFormats.merge(other.configurationFileFormats);
		this.name.merge(other.name);
		this.description.merge(other.description);
		this.groupId.merge(other.groupId);
		this.artifactId.merge(other.artifactId);
		this.version.merge(other.version);
		this.packageName.merge(other.packageName);
	}

	/**
	 * Validate the metadata.
	 */
	public void validate() {
		this.configuration.validate();
		this.dependencies.validate();

		Map<String, Repository> repositories = this.configuration.getEnv().getRepositories();
		Map<String, BillOfMaterials> boms = this.configuration.getEnv().getBoms();
		for (Dependency dependency : this.dependencies.getAll()) {
			if (dependency.getBom() != null && !boms.containsKey(dependency.getBom())) {
				throw new InvalidInitializrMetadataException("Dependency " + dependency + "defines an invalid BOM id "
						+ dependency.getBom() + ", available boms " + boms);
			}

			if (dependency.getRepository() != null && !repositories.containsKey(dependency.getRepository())) {
				throw new InvalidInitializrMetadataException(
						"Dependency " + dependency + "defines an invalid repository id " + dependency.getRepository()
								+ ", available repositories " + repositories);
			}
		}
		for (BillOfMaterials bom : boms.values()) {
			for (String r : bom.getRepositories()) {
				if (!repositories.containsKey(r)) {
					throw new InvalidInitializrMetadataException(
							bom + "defines an invalid repository id " + r + ", available repositories " + repositories);
				}
			}
			for (String b : bom.getAdditionalBoms()) {
				if (!boms.containsKey(b)) {
					throw new InvalidInitializrMetadataException(
							bom + " defines an invalid " + "additional bom id " + b + ", available boms " + boms);
				}
			}
			for (BillOfMaterials.Mapping m : bom.getMappings()) {
				for (String r : m.getRepositories()) {
					if (!repositories.containsKey(r)) {
						throw new InvalidInitializrMetadataException(m + " of " + bom
								+ "defines an invalid repository id " + r + ", available repositories " + repositories);
					}

				}
				for (String b : m.getAdditionalBoms()) {
					if (!boms.containsKey(b)) {
						throw new InvalidInitializrMetadataException(m + " of " + bom + " defines "
								+ "an invalid additional bom id " + b + ", available boms " + boms);
					}
				}
			}
		}
	}

	/**
	 * Update the available Spring Boot versions with the specified capabilities.
	 * <p>
	 * Every version is parsed so that the compatibility ranges can be updated, and a
	 * version that cannot be parsed is therefore rejected. Contrary to
	 * {@link #resolveBootVersion(String)}, this fails fast: a compatibility range that
	 * uses a wildcard would otherwise be silently resolved against an incomplete list of
	 * versions.
	 * @param versionsMetadata the Spring Boot boot versions metadata to use
	 * @throws InvalidVersionException if a version cannot be parsed
	 */
	public void updateSpringBootVersions(List<DefaultMetadataElement> versionsMetadata) {
		this.bootVersions.setContent(versionsMetadata);
		List<Version> bootVersions = this.bootVersions.getContent().stream().map((it) -> {
			String id = it.getId();
			Assert.state(id != null, "'id' must not be null");
			return Version.parse(id);
		}).toList();
		VersionParser parser = new VersionParser(bootVersions);
		this.dependencies.updateCompatibilityRange(parser);
		this.configuration.getEnv().updateCompatibilityRange(parser);
	}

	/**
	 * Resolve the specified platform version against the available
	 * {@link #getBootVersions() platform versions}. The minor and patch numbers can be
	 * omitted or specified as {@code x}, in which case the latest matching general
	 * availability version is returned, for instance {@code 4}, {@code 4.x.x} or
	 * {@code 4.0.x}. A {@code x} in any other position, such as {@code 4.x.3} or
	 * {@code 4.0.x-M1}, cannot be resolved. Any other value is parsed as is.
	 * <p>
	 * Contrary to {@link #updateSpringBootVersions(List)}, an available version that
	 * cannot be parsed is ignored rather than rejected, as this runs for every request:
	 * one such version in the metadata should not fail them all.
	 * @param text the platform version to resolve
	 * @return the resolved platform version or {@code null} if the version is invalid, if
	 * it uses a wildcard that cannot be resolved or if no available version matches
	 * @see VersionParser#resolveLatest(String)
	 */
	public @Nullable Version resolveBootVersion(String text) {
		return createVersionParser().resolveLatest(text);
	}

	/**
	 * Create a {@link VersionParser} for the currently available
	 * {@link #getBootVersions() platform versions}, ignoring any version that cannot be
	 * parsed. The parser is not cached as the available versions can be updated at any
	 * time, see {@link #updateSpringBootVersions(List)}.
	 * @return a parser for the available platform versions
	 */
	private VersionParser createVersionParser() {
		List<Version> bootVersions = this.bootVersions.getContent()
			.stream()
			.map(DefaultMetadataElement::getId)
			.filter(StringUtils::hasText)
			.map(Version::safeParse)
			.filter(Objects::nonNull)
			.toList();
		return new VersionParser(bootVersions);
	}

	/**
	 * Create an URL suitable to download Spring Boot cli for the specified version and
	 * extension.
	 * @param extension the required extension
	 * @return the download URL
	 */
	public String createCliDistributionURl(String extension) {
		String bootVersion = defaultId(this.bootVersions);
		return this.configuration.getEnv().getArtifactRepository() + "org/springframework/boot/spring-boot-cli/"
				+ bootVersion + "/spring-boot-cli-" + bootVersion + "-bin." + extension;
	}

	/**
	 * Create a {@link BillOfMaterials} for the spring boot BOM.
	 * @param bootVersion the Spring Boot version
	 * @param versionProperty the property that contains the version
	 * @return a new {@link BillOfMaterials} instance
	 */
	public BillOfMaterials createSpringBootBom(String bootVersion, String versionProperty) {
		BillOfMaterials bom = BillOfMaterials.create("org.springframework.boot", "spring-boot-dependencies",
				bootVersion);
		bom.setVersionProperty(VersionProperty.of(versionProperty));
		bom.setOrder(100);
		return bom;
	}

	/**
	 * Return the defaults for the capabilities defined on this instance.
	 * @return the default capabilities
	 */
	public Map<String, @Nullable Object> defaults() {
		Map<String, @Nullable Object> defaults = new LinkedHashMap<>();
		defaults.put("type", defaultId(this.types));
		defaults.put("bootVersion", defaultId(this.bootVersions));
		defaults.put("packaging", defaultId(this.packagings));
		defaults.put("javaVersion", defaultId(this.javaVersions));
		defaults.put("language", defaultId(this.languages));
		defaults.put("configurationFileFormat", defaultId(this.configurationFileFormats));
		defaults.put("groupId", this.groupId.getContent());
		defaults.put("artifactId", this.artifactId.getContent());
		defaults.put("version", this.version.getContent());
		defaults.put("name", this.name.getContent());
		defaults.put("description", this.description.getContent());
		defaults.put("packageName", this.packageName.getContent());
		return defaults;
	}

	private static @Nullable String defaultId(Defaultable<? extends DefaultMetadataElement> element) {
		DefaultMetadataElement defaultValue = element.getDefault();
		return (defaultValue != null) ? defaultValue.getId() : null;
	}

	private static class ArtifactIdCapability extends TextCapability {

		private final TextCapability nameCapability;

		ArtifactIdCapability(TextCapability nameCapability) {
			super("artifactId", "Artifact", "project coordinates (infer archive name)");
			this.nameCapability = nameCapability;
		}

		@Override
		public @Nullable String getContent() {
			String value = super.getContent();
			return (value != null) ? value : this.nameCapability.getContent();
		}

	}

	private static class PackageCapability extends TextCapability {

		private final TextCapability groupId;

		private final TextCapability artifactId;

		PackageCapability(TextCapability groupId, TextCapability artifactId) {
			super("packageName", "Package Name", "root package");
			this.groupId = groupId;
			this.artifactId = artifactId;
		}

		@Override
		public @Nullable String getContent() {
			String value = super.getContent();
			if (value != null) {
				return value;
			}
			else if (this.groupId.getContent() != null && this.artifactId.getContent() != null) {
				return InitializrConfiguration
					.cleanPackageName(this.groupId.getContent() + "." + this.artifactId.getContent());
			}
			return null;
		}

	}

}
