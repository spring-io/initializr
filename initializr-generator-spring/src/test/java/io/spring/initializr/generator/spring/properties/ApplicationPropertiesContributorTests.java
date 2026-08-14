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
import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationPropertiesContributor}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
class ApplicationPropertiesContributorTests {

	@TempDir
	@SuppressWarnings("NullAway.Init")
	Path directory;

	@Test
	void applicationConfigurationWithDefaultSettings() throws IOException {
		new ApplicationPropertiesContributor(new ApplicationProperties(), mavenProjectDescription())
			.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.isEmpty();
	}

	@Test
	void shouldAddStringProperty() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.application.name", "test");
		ApplicationPropertiesContributor contributor = new ApplicationPropertiesContributor(properties,
				mavenProjectDescription());
		contributor.contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.lines()
			.contains("spring.application.name=test");
	}

	@Test
	void shouldWriteTestSourceSetPropertiesToTestResources() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.section(SourceSet.TEST, null).add("spring.datasource.url", "jdbc:h2:mem:test");
		new ApplicationPropertiesContributor(properties, mavenProjectDescription()).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/test/resources/application.properties")
			.lines()
			.contains("spring.datasource.url=jdbc:h2:mem:test");
	}

	@Test
	void shouldWriteProfilePropertiesToProfileSpecificFile() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.section(SourceSet.MAIN, "dev").add("logging.level.root", "DEBUG");
		new ApplicationPropertiesContributor(properties, mavenProjectDescription()).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application-dev.properties")
			.lines()
			.contains("logging.level.root=DEBUG");
	}

	@Test
	void shouldWriteTestSourceSetProfileProperties() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.section(SourceSet.TEST, "integration").add("spring.application.name", "it");
		new ApplicationPropertiesContributor(properties, mavenProjectDescription()).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory))
			.textFile("src/test/resources/application-integration.properties")
			.lines()
			.contains("spring.application.name=it");
	}

	@Test
	void shouldNotWriteFileForEmptySection() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.section(SourceSet.TEST, null);
		new ApplicationPropertiesContributor(properties, mavenProjectDescription()).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).filePaths()
			.containsOnly("src/main/resources/application.properties");
	}

	@Test
	void shouldAlwaysWriteMainDefaultFileEvenWhenOnlySectionsHaveProperties() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.section(SourceSet.MAIN, "dev").add("test", "value");
		new ApplicationPropertiesContributor(properties, mavenProjectDescription()).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).filePaths()
			.containsOnly("src/main/resources/application.properties", "src/main/resources/application-dev.properties");
		assertThat(new ProjectStructure(this.directory)).textFile("src/main/resources/application.properties")
			.isEmpty();
	}

	@Test
	void shouldResolvePathsThroughBuildSystem() throws IOException {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.application.name", "main");
		properties.section(SourceSet.TEST).add("spring.application.name", "test");
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new CustomLayoutBuildSystem());
		description.setLanguage(new JavaLanguage());
		new ApplicationPropertiesContributor(properties, description).contribute(this.directory);
		assertThat(new ProjectStructure(this.directory)).filePaths()
			.containsOnly("custom-main/resources/application.properties",
					"custom-test/resources/application.properties");
	}

	private ProjectDescription mavenProjectDescription() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new MavenBuildSystem());
		description.setLanguage(new JavaLanguage());
		return description;
	}

	/**
	 * A {@link BuildSystem} with a non-standard layout, used to verify that the
	 * contributor resolves paths through {@link BuildSystem#getMainSource} and
	 * {@link BuildSystem#getTestSource} rather than hardcoding {@code src/main} and
	 * {@code src/test}.
	 */
	private static final class CustomLayoutBuildSystem implements BuildSystem {

		@Override
		public String id() {
			return "custom";
		}

		@Override
		public SourceStructure getMainSource(Path projectRoot, Language language) {
			return new SourceStructure(projectRoot.resolve("custom-main"), language);
		}

		@Override
		public SourceStructure getTestSource(Path projectRoot, Language language) {
			return new SourceStructure(projectRoot.resolve("custom-test"), language);
		}

	}

}
