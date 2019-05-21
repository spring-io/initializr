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

package io.spring.initializr.generator.buildsystem.gradle;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.test.io.TextTestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KotlinDslGradleSettingsWriter}.
 *
 * @author Jean-Baptiste Nizet
 */
class KotlinDslGradleSettingsWriterTests {

	@Test
	void gradleBuildWithMavenCentralPluginRepository() throws IOException {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        mavenCentral()", "        gradlePluginPortal()", "    }", "}");
	}

	@Test
	void gradleBuildWithPluginRepository() throws IOException {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("spring-milestones", "Spring Milestones",
				"https://repo.spring.io/milestone");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        maven { url = uri(\"https://repo.spring.io/milestone\") }",
				"        gradlePluginPortal()", "    }", "    resolutionStrategy {",
				"        eachPlugin {",
				"            if (requested.id.id == \"org.springframework.boot\") {",
				"                useModule(\"org.springframework.boot:spring-boot-gradle-plugin:${requested.version}\")",
				"            }", "        }", "    }", "}");
	}

	@Test
	void gradleBuildWithSnapshotPluginRepository() throws IOException {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("spring-snapshots", "Spring Snapshots",
				"https://repo.spring.io/snapshot", true);
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        maven { url = uri(\"https://repo.spring.io/snapshot\") }",
				"        gradlePluginPortal()", "    }", "    resolutionStrategy {",
				"        eachPlugin {",
				"            if (requested.id.id == \"org.springframework.boot\") {",
				"                useModule(\"org.springframework.boot:spring-boot-gradle-plugin:${requested.version}\")",
				"            }", "        }", "    }", "}");
	}

	@Test
	void artifactIdShouldBeUsedAsTheRootProjectName() throws Exception {
		GradleBuild build = new GradleBuild();
		build.setArtifact("my-application");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("rootProject.name = \"my-application\"");
	}

	private List<String> generateSettings(GradleBuild build) throws IOException {
		GradleSettingsWriter writer = new KotlinDslGradleSettingsWriter();
		StringWriter out = new StringWriter();
		writer.writeTo(new IndentingWriter(out), build);
		return TextTestUtils.readAllLines(out.toString());
	}

}
