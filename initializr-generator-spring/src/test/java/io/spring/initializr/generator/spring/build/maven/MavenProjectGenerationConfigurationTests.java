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
				.withDirectory(directory).withDescriptionCustomizer((description) -> {
					description.setBuildSystem(new MavenBuildSystem());
					description.setLanguage(new JavaLanguage());
				});
	}

	@Test
	void buildWriterIsContributed() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		this.projectTester.configure(description, (context) -> assertThat(context).hasSingleBean(BuildWriter.class)
				.getBean(BuildWriter.class).isInstanceOf(MavenBuildProjectContributor.class));
	}

	@Test
	void mavenWrapperIsContributedWhenGeneratingMavenProject() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).filePaths().contains("mvnw", "mvnw.cmd", ".mvn/wrapper/MavenWrapperDownloader.java",
				".mvn/wrapper/maven-wrapper.properties", ".mvn/wrapper/maven-wrapper.jar");
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
	@Deprecated
	void testStarterExcludesVintageEngineAndJUnitWithAppropriateVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.M4"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").lines().containsSequence("            <exclusions>",
				"                <exclusion>", "                    <groupId>org.junit.vintage</groupId>",
				"                    <artifactId>junit-vintage-engine</artifactId>", "                </exclusion>",
				"                <exclusion>", "                    <groupId>junit</groupId>",
				"                    <artifactId>junit</artifactId>", "                </exclusion>",
				"            </exclusions>");
	}

	@Test
	void testStarterExcludesVintageEngineWithCompatibleVersion() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.M5"));
		description.setLanguage(new JavaLanguage());
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").lines().containsSequence("            <exclusions>",
				"                <exclusion>", "                    <groupId>org.junit.vintage</groupId>",
				"                    <artifactId>junit-vintage-engine</artifactId>", "                </exclusion>",
				"            </exclusions>");
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
