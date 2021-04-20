/*
 * Copyright 2012-2021 the original author or authors.
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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GroovyDslGradleSettingsWriter}.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
class GroovyDslGradleSettingsWriterTests {

	@Test
	void gradleBuildWithMavenCentralPluginRepository() {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add("maven-central");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {", "        mavenCentral()",
				"        gradlePluginPortal()", "    }", "}");
	}

	@Test
	void gradleBuildWithoutPluginRepository() {
		GradleBuild build = new GradleBuild();
		List<String> lines = generateSettings(build);
		assertThat(lines).doesNotContain("pluginManagement");
	}

	@Test
	void gradleBuildWithPluginRepository() {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories().add(MavenRepository
				.withIdAndUrl("spring-milestones", "https://repo.spring.io/milestone").name("Spring Milestones"));
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        maven { url 'https://repo.spring.io/milestone' }", "        gradlePluginPortal()", "    }",
				"}");
	}

	@Test
	void gradleBuildWithSnapshotPluginRepository() {
		GradleBuild build = new GradleBuild();
		build.pluginRepositories()
				.add(MavenRepository.withIdAndUrl("spring-snapshots", "https://repo.spring.io/snapshot")
						.name("Spring Snapshots").onlySnapshots());
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("pluginManagement {", "    repositories {",
				"        maven { url 'https://repo.spring.io/snapshot' }", "        gradlePluginPortal()", "    }",
				"}");
	}

	@Test
	void gradleBuildWithPluginMappings() {
		GradleBuild build = new GradleBuild();
		build.settings()
				.mapPlugin("com.example",
						Dependency.withCoordinates("com.example", "gradle-plugin")
								.version(VersionReference.ofValue("1.0.0")).build())
				.mapPlugin("org.acme", Dependency.withCoordinates("org.acme.plugin", "gradle")
						.version(VersionReference.ofValue("2.0.0")).build());
		List<String> lines = generateSettings(build);
		assertThat(lines)
				.containsSequence(// @formatter:off
				"pluginManagement {",
				"    resolutionStrategy {",
				"        eachPlugin {",
				"            if (requested.id.id == 'com.example') {",
				"                useModule('com.example:gradle-plugin:1.0.0')",
				"            }",
				"            if (requested.id.id == 'org.acme') {",
				"                useModule('org.acme.plugin:gradle:2.0.0')",
				"            }",
				"        }",
				"    }",
				"}"); // @formatter:on
	}

	@Test
	void artifactIdShouldBeUsedAsTheRootProjectName() {
		GradleBuild build = new GradleBuild();
		build.settings().artifact("my-application");
		List<String> lines = generateSettings(build);
		assertThat(lines).containsSequence("rootProject.name = 'my-application'");
	}

	private List<String> generateSettings(GradleBuild build) {
		GradleSettingsWriter writer = new GroovyDslGradleSettingsWriter();
		StringWriter out = new StringWriter();
		writer.writeTo(new IndentingWriter(out), build);
		String[] lines = out.toString().split("\\r?\\n");
		return Arrays.asList(lines);
	}

}
