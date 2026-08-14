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

package io.spring.initializr.generator.spring.properties;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.properties.ApplicationProperties.SectionKey;
import org.jspecify.annotations.Nullable;

import org.springframework.util.Assert;

/**
 * Base {@link ProjectContributor} that contributes application configuration files to a
 * project. A file is written per source set and Spring profile that has properties,
 * following the {@code application[-{profile}]} convention, resolved against the
 * {@link BuildSystem}'s main or test {@link SourceStructure}. The file for the main
 * source set and the default profile is always written, even if it is empty.
 *
 * @author Denis A. Altoé Falqueto
 * @see ApplicationProperties#section(SourceSet, String)
 */
abstract class AbstractApplicationPropertiesContributor implements ProjectContributor {

	private final ApplicationProperties properties;

	private final BuildSystem buildSystem;

	private final Language language;

	private final String extension;

	/**
	 * Creates a new instance.
	 * @param properties the application properties to contribute
	 * @param description the description of the project, used to resolve the main and
	 * test source structures
	 * @param extension the extension of the configuration files to write, without the
	 * leading dot
	 */
	protected AbstractApplicationPropertiesContributor(ApplicationProperties properties, ProjectDescription description,
			String extension) {
		BuildSystem descriptionBuildSystem = description.getBuildSystem();
		Assert.state(descriptionBuildSystem != null, "'buildSystem' must not be null");
		Language descriptionLanguage = description.getLanguage();
		Assert.state(descriptionLanguage != null, "'language' must not be null");
		this.properties = properties;
		this.buildSystem = descriptionBuildSystem;
		this.language = descriptionLanguage;
		this.extension = extension;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		writeSection(projectRoot, SourceSet.MAIN, null, this.properties);
		for (Map.Entry<SectionKey, ApplicationProperties> entry : this.properties.getSections().entrySet()) {
			ApplicationProperties section = entry.getValue();
			if (section.hasProperties()) {
				writeSection(projectRoot, entry.getKey().sourceSet(), entry.getKey().profile(), section);
			}
		}
	}

	/**
	 * Writes the given properties using the given writer.
	 * @param properties the properties to write
	 * @param writer the writer to use
	 */
	protected abstract void write(ApplicationProperties properties, PrintWriter writer);

	private void writeSection(Path projectRoot, SourceSet sourceSet, @Nullable String profile,
			ApplicationProperties properties) throws IOException {
		Path output = resolveOutputFile(projectRoot, sourceSet, profile);
		if (!Files.exists(output)) {
			Files.createDirectories(output.getParent());
			Files.createFile(output);
		}
		try (PrintWriter writer = new PrintWriter(Files.newOutputStream(output, StandardOpenOption.APPEND), false,
				StandardCharsets.UTF_8)) {
			write(properties, writer);
		}
	}

	private Path resolveOutputFile(Path projectRoot, SourceSet sourceSet, @Nullable String profile) {
		SourceStructure sourceStructure = (sourceSet == SourceSet.TEST)
				? this.buildSystem.getTestSource(projectRoot, this.language)
				: this.buildSystem.getMainSource(projectRoot, this.language);
		String profileSuffix = (profile != null) ? "-" + profile : "";
		return sourceStructure.getResourcesDirectory().resolve("application" + profileSuffix + "." + this.extension);
	}

}
