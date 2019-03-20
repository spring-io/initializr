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

package io.spring.initializr.generator.spring.build.maven;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.test.io.TextTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MavenBuildProjectContributor}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
class MavenBuildProjectContributorTests {

	@Test
	void mavenBuildIsContributedInProjectStructure(@TempDir Path projectDir)
			throws IOException {
		MavenBuild build = new MavenBuild();
		new MavenBuildProjectContributor(build,
				IndentingWriterFactory.withDefaultSettings()).contribute(projectDir);
		Path buildGradle = projectDir.resolve("pom.xml");
		assertThat(buildGradle).isRegularFile();
	}

	@Test
	void pomIsContributedToProject() throws Exception {
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.parent("org.springframework.boot", "spring-boot-starter-parent",
				"2.1.0.RELEASE");
		List<String> lines = generatePom(build);
		assertThat(lines).containsSequence("    <parent>",
				"        <groupId>org.springframework.boot</groupId>",
				"        <artifactId>spring-boot-starter-parent</artifactId>",
				"        <version>2.1.0.RELEASE</version>");
	}

	@Test
	void pomIsContributedUsingMavenContentId() throws Exception {
		IndentingWriterFactory indentingWriterFactory = IndentingWriterFactory
				.create(new SimpleIndentStrategy("    "), (factory) -> factory
						.indentingStrategy("maven", new SimpleIndentStrategy("\t")));
		MavenBuild build = new MavenBuild();
		build.setGroup("com.example.demo");
		build.setArtifact("demo");
		build.parent("org.springframework.boot", "spring-boot-starter-parent",
				"2.1.0.RELEASE");
		List<String> lines = generatePom(build, indentingWriterFactory);
		assertThat(lines).containsSequence("\t<parent>",
				"\t\t<groupId>org.springframework.boot</groupId>",
				"\t\t<artifactId>spring-boot-starter-parent</artifactId>",
				"\t\t<version>2.1.0.RELEASE</version>");
	}

	private List<String> generatePom(MavenBuild mavenBuild) throws Exception {
		return generatePom(mavenBuild, IndentingWriterFactory.withDefaultSettings());
	}

	private List<String> generatePom(MavenBuild mavenBuild,
			IndentingWriterFactory indentingWriterFactory) throws Exception {
		StringWriter writer = new StringWriter();
		new MavenBuildProjectContributor(mavenBuild, indentingWriterFactory)
				.writeBuild(writer);
		return TextTestUtils.readAllLines(writer.toString());
	}

}
