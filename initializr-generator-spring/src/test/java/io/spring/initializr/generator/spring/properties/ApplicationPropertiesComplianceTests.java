/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.SourceSet;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.configuration.format.ConfigurationFileFormat;
import io.spring.initializr.generator.configuration.format.properties.PropertiesFormat;
import io.spring.initializr.generator.configuration.format.yaml.YamlFormat;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.spring.AbstractComplianceTests;
import io.spring.initializr.generator.test.project.ProjectStructure;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import org.springframework.core.io.ClassPathResource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Application properties compliance tests.
 *
 * @author Sijun Yang
 * @author Moritz Halbritter
 */
class ApplicationPropertiesComplianceTests extends AbstractComplianceTests {

	private static final BuildSystem maven = BuildSystem.forId(MavenBuildSystem.ID);

	private static final Language java = new JavaLanguage();

	/**
	 * The content written for {@code spring.application.name=app-name}, keyed by format
	 * id.
	 */
	private static final Map<String, List<String>> EXPECTED_CONTENT = Map.of(PropertiesFormat.ID,
			List.of("spring.application.name=app-name"), YamlFormat.ID,
			List.of("spring:", "  application:", "    name: app-name"));

	static Stream<ConfigurationFileFormat> formats() {
		return Stream.of(ConfigurationFileFormat.forId(PropertiesFormat.ID),
				ConfigurationFileFormat.forId(YamlFormat.ID));
	}

	@ParameterizedTest
	@MethodSource("formats")
	void shouldGenerateApplicationProperties(ConfigurationFileFormat format) {
		ProjectStructure project = generateProject(java, maven, "2.4.1",
				(description) -> description.setConfigurationFileFormat(format));
		assertThat(project).filePaths().contains(mainFile(format));
	}

	@ParameterizedTest
	@MethodSource("formats")
	void shouldWriteCustomProperties(ConfigurationFileFormat format) {
		ProjectStructure project = generateProject(java, maven, "2.4.1",
				(description) -> description.setConfigurationFileFormat(format),
				(projectGenerationContext) -> projectGenerationContext.registerBean(
						ApplicationPropertiesCustomizer.class,
						() -> (properties) -> properties.add("spring.application.name", "app-name")));
		String path = "project/properties/%s/application%s.gen".formatted(format, format.fileExtension());
		assertThat(project).textFile(mainFile(format))
			.as("Resource " + path)
			.hasSameContentAs(new ClassPathResource(path));
	}

	@ParameterizedTest
	@MethodSource("formats")
	void shouldWriteSourceSetAndProfileFiles(ConfigurationFileFormat format) {
		ProjectStructure project = generateProject(java, maven, "2.4.1",
				(description) -> description.setConfigurationFileFormat(format),
				(projectGenerationContext) -> projectGenerationContext
					.registerBean(ApplicationPropertiesCustomizer.class, () -> (properties) -> {
						properties.file(SourceSet.TEST).add("spring.application.name", "app-name");
						properties.profile("dev").add("spring.application.name", "app-name");
					}));
		List<String> expectedContent = EXPECTED_CONTENT.get(format.id());
		assertThat(project).textFile("src/test/resources/application%s".formatted(format.fileExtension()))
			.lines()
			.containsExactlyElementsOf(expectedContent);
		assertThat(project).textFile("src/main/resources/application-dev%s".formatted(format.fileExtension()))
			.lines()
			.containsExactlyElementsOf(expectedContent);
	}

	private String mainFile(ConfigurationFileFormat format) {
		return "src/main/resources/application%s".formatted(format.fileExtension());
	}

}
