/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.groovy;

import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.spring.code.SourceCodeProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GroovyProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class GroovyProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
			.withConfiguration(SourceCodeProjectGenerationConfiguration.class,
					GroovyProjectGenerationConfiguration.class)
			.withDirectory(directory)
			.withDescriptionCustomizer((description) -> {
				description.setLanguage(new GroovyLanguage());
				if (description.getPlatformVersion() == null) {
					description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
				}
				description.setBuildSystem(new MavenBuildSystem());
			});
	}

	@Test
	void mainClassIsContributed() {
		ProjectStructure project = this.projectTester.generate(new MutableProjectDescription());
		assertThat(project).containsFiles("src/main/groovy/com/example/demo/DemoApplication.groovy");
	}

	@Test
	void testClassIsContributedWithJUnit5() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.RELEASE"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("src/test/groovy/com/example/demo/DemoApplicationTests.groovy")
			.containsExactly("package com.example.demo", "", "import org.junit.jupiter.api.Test",
					"import org.springframework.boot.test.context.SpringBootTest", "", "@SpringBootTest",
					"class DemoApplicationTests {", "", "    @Test", "    void contextLoads() {", "    }", "", "}");
	}

	@Test
	void servletInitializerIsContributedWhenGeneratingProjectThatUsesWarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackaging(new WarPackaging());
		description.setApplicationName("Demo2Application");
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("src/main/groovy/com/example/demo/ServletInitializer.groovy")
			.containsExactly("package com.example.demo", "",
					"import org.springframework.boot.builder.SpringApplicationBuilder",
					"import org.springframework.boot.web.servlet.support.SpringBootServletInitializer", "",
					"class ServletInitializer extends SpringBootServletInitializer {", "", "    @Override",
					"    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {",
					"        application.sources(Demo2Application)", "    }", "", "}");
	}

}
