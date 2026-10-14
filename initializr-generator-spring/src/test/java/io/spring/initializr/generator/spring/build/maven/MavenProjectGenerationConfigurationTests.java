/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.build.maven;

import java.nio.file.Path;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.spring.build.BuildProjectGenerationConfiguration;
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
 * Tests for {@link MavenProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class MavenProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
			.withConfiguration(BuildProjectGenerationConfiguration.class, MavenProjectGenerationConfiguration.class)
			.withBean(InitializrMetadata.class, () -> InitializrMetadataTestBuilder.withDefaults().build())
			.withDirectory(directory)
			.withDescriptionCustomizer((description) -> {
				description.setBuildSystem(new MavenBuildSystem());
				description.setLanguage(new JavaLanguage());
			});
	}

	@Test
	void buildWriterIsContributed() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		this.projectTester.configure(description,
				(context) -> assertThat(context).hasSingleBean(BuildWriter.class)
					.getBean(BuildWriter.class)
					.isInstanceOf(MavenBuildProjectContributor.class));
	}

	@Test
	void mavenWrapperIsContributedWhenGeneratingMavenProject() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).filePaths().contains("mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.properties");
	}

	@Test
	void mavenPomIsContributedWhenGeneratingMavenProject() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).filePaths().contains("pom.xml");
	}

	@Test
	void warPackagingIsUsedWhenBuildingProjectThatUsesWarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		description.setPackaging(new WarPackaging());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").containsOnlyOnce("    <packaging>war</packaging>");
	}

	@Test
	void testStarterDoesNotExcludesVintageEngineAndJUnitWithIncompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.6.RELEASE"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").doesNotContain("            <exclusions>");
	}

	@Test
	void testStarterDoesNotExcludeVintageEngineWith24Snapshot() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0-SNAPSHOT"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").doesNotContain("            <exclusions>");
	}

	@Test
	void testStarterDoesNotExcludeVintageEngineWith24Milestone() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.4.0-M1"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").doesNotContain("            <exclusions>");
	}

}
