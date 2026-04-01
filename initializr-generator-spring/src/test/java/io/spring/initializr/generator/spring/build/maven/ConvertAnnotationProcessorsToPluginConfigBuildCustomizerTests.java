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

package io.spring.initializr.generator.spring.build.maven;

import java.io.IOException;
import java.io.StringWriter;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ConvertAnnotationProcessorsToPluginConfigBuildCustomizer}.
 *
 * @author Moritz Halbritter
 */
class ConvertAnnotationProcessorsToPluginConfigBuildCustomizerTests {

	private final ConvertAnnotationProcessorsToPluginConfigBuildCustomizer customizer = new ConvertAnnotationProcessorsToPluginConfigBuildCustomizer();

	@Test
	void annotationProcessorIsConvertedToDefaultCompileExecution() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("configuration-processor",
					Dependency.withCoordinates("org.springframework.boot", "spring-boot-configuration-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).doesNotContain("<dependency>");
		assertThat(pom).containsIgnoringWhitespaces("""
				<execution>
					<id>default-compile</id>
					<phase>compile</phase>
					<goals>
						<goal>compile</goal>
					</goals>
					<configuration>
						<annotationProcessorPaths>
							<path>
								<groupId>org.springframework.boot</groupId>
								<artifactId>spring-boot-configuration-processor</artifactId>
							</path>
						</annotationProcessorPaths>
					</configuration>
				</execution>
				""");
	}

	@Test
	void testAnnotationProcessorIsConvertedToDefaultTestCompileExecution() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("my-processor",
					Dependency.withCoordinates("com.example", "my-annotation-processor")
						.scope(DependencyScope.TEST_ANNOTATION_PROCESSOR)
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).doesNotContain("<dependency>");
		assertThat(pom).containsIgnoringWhitespaces("""
				<execution>
					<id>default-testCompile</id>
					<phase>test-compile</phase>
					<goals>
						<goal>testCompile</goal>
					</goals>
					<configuration>
						<annotationProcessorPaths>
							<path>
								<groupId>com.example</groupId>
								<artifactId>my-annotation-processor</artifactId>
							</path>
						</annotationProcessorPaths>
					</configuration>
				</execution>
				""");
	}

	@Test
	void annotationProcessorAndTestAnnotationProcessorEndUpInSeparateExecutions() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("processor",
					Dependency.withCoordinates("com.example", "my-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.build());
		build.dependencies()
			.add("test-processor",
					Dependency.withCoordinates("com.example", "my-test-processor")
						.scope(DependencyScope.TEST_ANNOTATION_PROCESSOR)
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).doesNotContain("<dependency>");
		assertThat(pom).containsIgnoringWhitespaces("""
				<execution>
					<id>default-compile</id>
					<phase>compile</phase>
					<goals>
						<goal>compile</goal>
					</goals>
					<configuration>
						<annotationProcessorPaths>
							<path>
								<groupId>com.example</groupId>
								<artifactId>my-processor</artifactId>
							</path>
						</annotationProcessorPaths>
					</configuration>
				</execution>
				""");
		assertThat(pom).containsIgnoringWhitespaces("""
				<execution>
					<id>default-testCompile</id>
					<phase>test-compile</phase>
					<goals>
						<goal>testCompile</goal>
					</goals>
					<configuration>
						<annotationProcessorPaths>
							<path>
								<groupId>com.example</groupId>
								<artifactId>my-test-processor</artifactId>
							</path>
						</annotationProcessorPaths>
					</configuration>
				</execution>
				""");
	}

	@Test
	void multipleAnnotationProcessorsAreConvertedToSingleAnnotationProcessorPathsElement() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("processor-a",
					Dependency.withCoordinates("com.example", "processor-a")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.build());
		build.dependencies()
			.add("processor-b",
					Dependency.withCoordinates("com.example", "processor-b")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).doesNotContain("<dependency>");
		assertThat(pom).containsIgnoringWhitespaces("""
				<execution>
					<id>default-compile</id>
					<phase>compile</phase>
					<goals>
						<goal>compile</goal>
					</goals>
					<configuration>
						<annotationProcessorPaths>
							<path>
								<groupId>com.example</groupId>
								<artifactId>processor-a</artifactId>
							</path>
							<path>
								<groupId>com.example</groupId>
								<artifactId>processor-b</artifactId>
							</path>
						</annotationProcessorPaths>
					</configuration>
				</execution>
				""");
	}

	@Test
	void multipleTestAnnotationProcessorsAreConvertedToSingleAnnotationProcessorPathsElement() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("processor-a",
					Dependency.withCoordinates("com.example", "processor-a")
						.scope(DependencyScope.TEST_ANNOTATION_PROCESSOR)
						.build());
		build.dependencies()
			.add("processor-b",
					Dependency.withCoordinates("com.example", "processor-b")
						.scope(DependencyScope.TEST_ANNOTATION_PROCESSOR)
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).doesNotContain("<dependency>");
		assertThat(pom).containsIgnoringWhitespaces("""
				<execution>
					<id>default-testCompile</id>
					<phase>test-compile</phase>
					<goals>
						<goal>testCompile</goal>
					</goals>
					<configuration>
						<annotationProcessorPaths>
							<path>
								<groupId>com.example</groupId>
								<artifactId>processor-a</artifactId>
							</path>
							<path>
								<groupId>com.example</groupId>
								<artifactId>processor-b</artifactId>
							</path>
						</annotationProcessorPaths>
					</configuration>
				</execution>
				""");
	}

	@Test
	void dependencyWithoutScopeIsNotConverted() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("web", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-web").build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).contains("<dependency>");
		assertThat(pom).doesNotContain("<annotationProcessorPaths>");
	}

	@Test
	void dependencyWithCompileScopeIsNotConverted() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("web",
					Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-web")
						.scope(DependencyScope.COMPILE)
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).contains("<dependency>");
		assertThat(pom).doesNotContain("<annotationProcessorPaths>");
	}

	@Test
	void annotationProcessorWithVersionValueHasVersionInPath() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("my-processor",
					Dependency.withCoordinates("com.example", "my-annotation-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.version(VersionReference.ofValue("1.2.3"))
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).containsIgnoringWhitespaces("""
				<path>
					<groupId>com.example</groupId>
					<artifactId>my-annotation-processor</artifactId>
					<version>1.2.3</version>
				</path>
				""");
	}

	@Test
	void annotationProcessorWithVersionPropertyHasPropertyExpressionInPath() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("my-processor",
					Dependency.withCoordinates("com.example", "my-annotation-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.version(VersionReference.ofProperty(VersionProperty.of("my-processor.version")))
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).containsIgnoringWhitespaces("""
				<path>
					<groupId>com.example</groupId>
					<artifactId>my-annotation-processor</artifactId>
					<version>${my-processor.version}</version>
				</path>
				""");
	}

	@Test
	void annotationProcessorWithClassifierHasClassifierInPath() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("my-processor",
					Dependency.withCoordinates("com.example", "my-annotation-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.classifier("tests")
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).containsIgnoringWhitespaces("""
				<path>
					<groupId>com.example</groupId>
					<artifactId>my-annotation-processor</artifactId>
					<classifier>tests</classifier>
				</path>
				""");
	}

	@Test
	void annotationProcessorWithTypeHasTypeInPath() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("my-processor",
					Dependency.withCoordinates("com.example", "my-annotation-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.type("pom")
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).containsIgnoringWhitespaces("""
				<path>
					<groupId>com.example</groupId>
					<artifactId>my-annotation-processor</artifactId>
					<type>pom</type>
				</path>
				""");
	}

	@Test
	void annotationProcessorWithExclusionsHasExclusionsInPath() throws IOException {
		MavenBuild build = new MavenBuild();
		build.dependencies()
			.add("my-processor",
					Dependency.withCoordinates("com.example", "my-annotation-processor")
						.scope(DependencyScope.ANNOTATION_PROCESSOR)
						.exclusions(new Dependency.Exclusion("com.example", "excluded-artifact"))
						.build());
		this.customizer.customize(build);
		String pom = generatePom(build);
		assertThat(pom).containsIgnoringWhitespaces("""
				<path>
					<groupId>com.example</groupId>
					<artifactId>my-annotation-processor</artifactId>
					<exclusions>
						<exclusion>
							<groupId>com.example</groupId>
							<artifactId>excluded-artifact</artifactId>
						</exclusion>
					</exclusions>
				</path>
				""");
	}

	private String generatePom(MavenBuild build) throws IOException {
		StringWriter writer = new StringWriter();
		new MavenBuildProjectContributor(build, IndentingWriterFactory.withDefaultSettings()).writeBuild(writer);
		return writer.toString();
	}

}
