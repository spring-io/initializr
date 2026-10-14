/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.SourceSet;
import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import org.springframework.util.Assert;

/**
 * Base {@link ProjectContributor} that writes the application configuration files of a
 * project, one per {@link ApplicationPropertiesFile} that has properties. The default
 * file of the main source set is written even when empty.
 *
 * @author Denis A. Altoé Falqueto
 * @author Moritz Halbritter
 * @see ApplicationProperties
 */
abstract class AbstractApplicationPropertiesContributor implements ProjectContributor {

	private final ApplicationProperties properties;

	private final ProjectDescription description;

	private final ConfigurationFileFormat format;

	/**
	 * Creates a new instance.
	 * @param properties the application properties to contribute
	 * @param description the description of the project, used to resolve the source
	 * structures
	 * @param format the format of the written files, defining their extension
	 */
	protected AbstractApplicationPropertiesContributor(ApplicationProperties properties, ProjectDescription description,
			ConfigurationFileFormat format) {
		this.properties = properties;
		this.description = description;
		this.format = format;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		BuildSystem buildSystem = this.description.getBuildSystem();
		Assert.state(buildSystem != null, "'buildSystem' must not be null");
		Language language = this.description.getLanguage();
		Assert.state(language != null, "'language' must not be null");
		for (SourceSet sourceSet : SourceSet.values()) {
			contribute(buildSystem.getSource(projectRoot, language, sourceSet), sourceSet);
		}
	}

	private void contribute(SourceStructure sourceStructure, SourceSet sourceSet) throws IOException {
		for (Map.Entry<ProfileName, ApplicationPropertiesFile> entry : this.properties.files(sourceSet).entrySet()) {
			ProfileName profile = entry.getKey();
			ApplicationPropertiesFile file = entry.getValue();
			// Generated projects always ship a main application file, even an empty one.
			// Every other file is written only if it has properties.
			boolean mainDefault = (sourceSet == SourceSet.MAIN) && ProfileName.DEFAULT.equals(profile);
			if (file.isEmpty() && !mainDefault) {
				continue;
			}
			writeFile(sourceStructure, profile, file);
		}
	}

	private void writeFile(SourceStructure sourceStructure, ProfileName profile, ApplicationPropertiesFile file)
			throws IOException {
		Path output = resolveOutputFile(sourceStructure, profile);
		if (!Files.exists(output)) {
			Path parent = output.getParent();
			if (parent != null) {
				Files.createDirectories(parent);
			}
			Files.createFile(output);
		}
		try (PrintWriter writer = new PrintWriter(Files.newOutputStream(output, StandardOpenOption.APPEND), false,
				StandardCharsets.UTF_8)) {
			writeProperties(file.properties(), writer);
		}
	}

	/**
	 * Writes the given properties in the format of this contributor.
	 * @param properties the properties to write, keyed by their dotted name
	 * @param writer the writer to use
	 */
	protected abstract void writeProperties(Map<String, Object> properties, PrintWriter writer);

	private Path resolveOutputFile(SourceStructure sourceStructure, ProfileName profile) {
		String fileName = "application" + profile.fileSuffix() + this.format.fileExtension();
		return sourceStructure.getResourcesDirectory().resolve(fileName);
	}

}
