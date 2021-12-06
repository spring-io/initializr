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

package io.spring.initializr.generator.spring.configuration;

import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.spring.build.gradle.GradleProjectGenerationConfiguration;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ApplicationConfigurationProjectGenerationConfiguration}
 *
 * @author Linda Navarette
 */
class ApplicationConfigurationProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
				.withConfiguration(ApplicationConfigurationProjectGenerationConfiguration.class,
						GradleProjectGenerationConfiguration.class)
				.withDirectory(directory)
				.withBean(InitializrMetadata.class, () -> InitializrMetadataTestBuilder.withDefaults().build());
	}

	@Test
	void applicationConfigurationResourcesAreIncludedInSingleModuleBuild() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		description.setLanguage(new JavaLanguage());
		description.setBuildSystem(new GradleBuildSystem());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).containsFiles("src/main/resources/application.properties");
	}

	@Test
	void applicationConfigurationResourcesAreIncludedInMultiModuleBuild() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0"));
		description.setLanguage(new JavaLanguage());
		description.setBuildSystem(new GradleBuildSystem(GradleBuildSystem.DIALECT_GROOVY, "foo"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).containsFiles("foo/src/main/resources/application.properties");
	}

}
