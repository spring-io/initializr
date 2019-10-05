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

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Gradle3BuildWriter}.
 *
 * @author Stephane Nicoll
 */
class Gradle3BuildWriterTests {

	@Test
	void gradleBuildWithAnnotationProcessorDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("annotation-processor", "org.springframework.boot",
				"spring-boot-configuration-processor", DependencyScope.ANNOTATION_PROCESSOR);
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    compileOnly 'org.springframework.boot:spring-boot-configuration-processor'", "}");
	}

	@Test
	void gradleBuildWithCompileDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("root", "org.springframework.boot", "spring-boot-starter", DependencyScope.COMPILE);
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    compile 'org.springframework.boot:spring-boot-starter'", "}");
	}

	@Test
	void gradleBuildWithNoScopeDependencyDefaultsToCompile() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    compile 'org.springframework.boot:spring-boot-starter'", "}");
	}

	@Test
	void gradleBuildWithRuntimeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("driver", Dependency.withCoordinates("com.example", "jdbc-driver")
				.version(VersionReference.ofValue("1.0.0")).scope(DependencyScope.RUNTIME));
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {", "    runtime 'com.example:jdbc-driver:1.0.0'", "}");
	}

	@Test
	void gradleBuildWithProvidedRuntimeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("tomcat", "org.springframework.boot", "spring-boot-starter-tomcat",
				DependencyScope.PROVIDED_RUNTIME);
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'", "}");
	}

	@Test
	void gradleBuildWithTestCompileDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("test", "org.springframework.boot", "spring-boot-starter-test",
				DependencyScope.TEST_COMPILE);
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    testCompile 'org.springframework.boot:spring-boot-starter-test'", "}");
	}

	@Test
	void gradleBuildWithCompileOnlyDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("test", "org.springframework.boot", "spring-boot-starter-foobar",
				DependencyScope.COMPILE_ONLY);
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    compileOnly 'org.springframework.boot:spring-boot-starter-foobar'", "}");
	}

	@Test
	void gradleBuildWithTestRuntimeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("embed-mongo", "de.flapdoodle.embed", "de.flapdoodle.embed.mongo",
				DependencyScope.TEST_RUNTIME);
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    testRuntime 'de.flapdoodle.embed:de.flapdoodle.embed.mongo'", "}");
	}

	@Test
	void gradleBuildWithNonNullArtifactTypeDependency() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("root", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter")
				.scope(DependencyScope.COMPILE).type("tar.gz"));
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    compile 'org.springframework.boot:spring-boot-starter@tar.gz'", "}");
	}

	@Test
	void gradleBuildWithCustomDependencyConfiguration() {
		GradleBuild build = new GradleBuild();
		build.dependencies().add("test",
				GradleDependency.withCoordinates("org.springframework.boot", "spring-boot-starter-foobar")
						.scope(DependencyScope.RUNTIME).configuration("myRuntime"));
		List<String> lines = generateBuild(build);
		assertThat(lines).containsSequence("dependencies {",
				"    myRuntime 'org.springframework.boot:spring-boot-starter-foobar'", "}");
	}

	private List<String> generateBuild(GradleBuild build) {
		Gradle3BuildWriter writer = new Gradle3BuildWriter();
		StringWriter out = new StringWriter();
		writer.writeTo(new IndentingWriter(out), build);
		String[] lines = out.toString().split("\\r?\\n");
		return Arrays.asList(lines);
	}

}
