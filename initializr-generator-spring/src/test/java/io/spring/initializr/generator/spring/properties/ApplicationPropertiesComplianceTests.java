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

import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.configuration.format.properties.PropertiesFormat;
import io.spring.initializr.generator.configuration.format.yaml.YamlFormat;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.AbstractComplianceTests;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Application properties compliance tests.
 *
 * @author Sijun Yang
 */
class ApplicationPropertiesComplianceTests extends AbstractComplianceTests {

	private static final BuildSystem maven = BuildSystem.forId(MavenBuildSystem.ID);

	private static final Language java = new JavaLanguage();

	static Stream<Arguments> parameters() {
		return Stream.of(
				Arguments.arguments(ConfigurationFileFormat.forId(PropertiesFormat.ID), "application.properties"),
				Arguments.arguments(ConfigurationFileFormat.forId(YamlFormat.ID), "application.yml"));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void applicationPropertiesGenerated(ConfigurationFileFormat format, String fileName) {
		ProjectStructure project = generateProject(java, maven, "2.4.1",
				(description) -> description.setConfigurationFileFormat(format));
		assertThat(project).filePaths().contains(String.format("src/main/resources/%s", fileName));
	}

	@ParameterizedTest
	@MethodSource("parameters")
	void applicationPropertiesWithCustomProperties(ConfigurationFileFormat format, String fileName) {
		ProjectStructure project = generateProject(java, maven, "2.4.1",
				(description) -> description.setConfigurationFileFormat(format),
				(projectGenerationContext) -> projectGenerationContext.registerBean(
						ApplicationPropertiesCustomizer.class,
						() -> (properties) -> properties.add("spring.application.name", "app-name")));
		String path = "project/properties/" + format + "/" + getAssertFileName(fileName);
		assertThat(project).textFile(String.format("src/main/resources/%s", fileName))
			.as("Resource " + path)
			.hasSameContentAs(new ClassPathResource(path));
	}

	private String getAssertFileName(String fileName) {
		return fileName + ".gen";
	}

}
