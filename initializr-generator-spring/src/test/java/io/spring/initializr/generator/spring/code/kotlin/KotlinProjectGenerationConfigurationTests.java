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

package io.spring.initializr.generator.spring.code.kotlin;

import java.nio.file.Path;
import java.util.Collections;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.spring.build.BuildProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.build.maven.MavenProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.code.SourceCodeProjectGenerationConfiguration;
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
 * Tests for {@link KotlinProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class KotlinProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
				.withConfiguration(SourceCodeProjectGenerationConfiguration.class,
						KotlinProjectGenerationConfiguration.class, BuildProjectGenerationConfiguration.class,
						MavenProjectGenerationConfiguration.class)
				.withDirectory(directory).withBean(InitializrMetadata.class, () -> {
					io.spring.initializr.metadata.Dependency dependency = io.spring.initializr.metadata.Dependency
							.withId("foo");
					dependency.setFacets(Collections.singletonList("json"));
					return InitializrMetadataTestBuilder.withDefaults().addDependencyGroup("test", dependency).build();
				}).withDescriptionCustomizer((description) -> {
					description.setLanguage(new KotlinLanguage());
					if (description.getPlatformVersion() == null) {
						description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
					}
					description.setBuildSystem(new MavenBuildSystem());
				});
	}

	@Test
	void kotlinVersionFallbacksToMetadataIfNotPresent() {
		this.projectTester.configure(new MutableProjectDescription(),
				(context) -> assertThat(context.getBean(KotlinProjectSettings.class).getVersion()).isEqualTo("1.1.1"));
	}

	@Test
	void kotlinVersionResolverIsUsedIfPresent() {
		this.projectTester.withBean(KotlinVersionResolver.class, () -> (description) -> "0.9.12").configure(
				new MutableProjectDescription(),
				(context) -> assertThat(context.getBean(KotlinProjectSettings.class).getVersion()).isEqualTo("0.9.12"));
	}

	@Test
	void mainClassIsContributedWhenGeneratingProject() {
		ProjectStructure project = this.projectTester.generate(new MutableProjectDescription());
		assertThat(project).containsFiles("src/main/kotlin/com/example/demo/DemoApplication.kt");
	}

	@Test
	void testClassIsContributedWithJunit4() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.4.RELEASE"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("src/test/kotlin/com/example/demo/DemoApplicationTests.kt").containsExactly(
				"package com.example.demo", "", "import org.junit.Test", "import org.junit.runner.RunWith",
				"import org.springframework.boot.test.context.SpringBootTest",
				"import org.springframework.test.context.junit4.SpringRunner", "", "@RunWith(SpringRunner::class)",
				"@SpringBootTest", "class DemoApplicationTests {", "", "    @Test", "    fun contextLoads() {", "    }",
				"", "}");
	}

	@Test
	void testClassIsContributedWithJunit5() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.RELEASE"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("src/test/kotlin/com/example/demo/DemoApplicationTests.kt").containsExactly(
				"package com.example.demo", "", "import org.junit.jupiter.api.Test",
				"import org.springframework.boot.test.context.SpringBootTest", "", "@SpringBootTest",
				"class DemoApplicationTests {", "", "    @Test", "    fun contextLoads() {", "    }", "", "}");
	}

	@Test
	void servletInitializerIsContributedWhenGeneratingProjectThatUsesWarPackaging() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setPackaging(new WarPackaging());
		description.setApplicationName("KotlinDemoApplication");
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("src/main/kotlin/com/example/demo/ServletInitializer.kt").containsExactly(
				"package com.example.demo", "", "import org.springframework.boot.builder.SpringApplicationBuilder",
				"import org.springframework.boot.web.servlet.support.SpringBootServletInitializer", "",
				"class ServletInitializer : SpringBootServletInitializer() {", "",
				"    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {",
				"        return application.sources(KotlinDemoApplication::class.java)", "    }", "", "}");
	}

	@Test
	void jacksonKotlinModuleShouldBeAddedWhenJsonFacetPresent() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.addDependency("foo", Dependency.withCoordinates("com.example", "foo"));
		ProjectStructure project = this.projectTester.generate(description);
		assertThat(project).textFile("pom.xml").contains("        <dependency>",
				"            <groupId>com.fasterxml.jackson.module</groupId>",
				"            <artifactId>jackson-module-kotlin</artifactId>", "        </dependency>");
	}

}
