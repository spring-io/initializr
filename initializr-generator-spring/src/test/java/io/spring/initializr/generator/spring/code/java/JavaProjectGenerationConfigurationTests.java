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

package io.spring.initializr.generator.spring.code.java;

import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.code.SourceCodeProjectGenerationConfiguration;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JavaProjectGenerationConfiguration}.
 *
 * @author Stephane Nicoll
 */
class JavaProjectGenerationConfigurationTests {

	private ProjectAssetTester projectTester;

	@BeforeEach
	void setup(@TempDir Path directory) {
		this.projectTester = new ProjectAssetTester().withIndentingWriterFactory()
				.withConfiguration(SourceCodeProjectGenerationConfiguration.class,
						JavaProjectGenerationConfiguration.class)
				.withDirectory(directory).withDescriptionCustomizer((description) -> {
					description.setLanguage(new JavaLanguage());
					if (description.getPlatformVersion() == null) {
						description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
					}
					description.setBuildSystem(new MavenBuildSystem());
				});
	}

	@Test
	void mainClassIsContributed() {
		ProjectDescription description = new ProjectDescription();
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.contains("src/main/java/com/example/demo/DemoApplication.java");
	}

	@Test
	void testClassIsContributedWithJUnit4() {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse("2.1.4.RELEASE"));
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.contains("src/test/java/com/example/demo/DemoApplicationTests.java");
		List<String> lines = projectStructure
				.readAllLines("src/test/java/com/example/demo/DemoApplicationTests.java");
		assertThat(lines).containsExactly("package com.example.demo;", "",
				"import org.junit.Test;", "import org.junit.runner.RunWith;",
				"import org.springframework.boot.test.context.SpringBootTest;",
				"import org.springframework.test.context.junit4.SpringRunner;", "",
				"@RunWith(SpringRunner.class)", "@SpringBootTest",
				"public class DemoApplicationTests {", "", "    @Test",
				"    public void contextLoads() {", "    }", "", "}");
	}

	@Test
	void testClassIsContributedWithJUnit5() {
		ProjectDescription description = new ProjectDescription();
		description.setPlatformVersion(Version.parse("2.2.0.RELEASE"));
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.contains("src/test/java/com/example/demo/DemoApplicationTests.java");
		List<String> lines = projectStructure
				.readAllLines("src/test/java/com/example/demo/DemoApplicationTests.java");
		assertThat(lines).containsExactly("package com.example.demo;", "",
				"import org.junit.jupiter.api.Test;",
				"import org.springframework.boot.test.context.SpringBootTest;", "",
				"@SpringBootTest", "class DemoApplicationTests {", "", "    @Test",
				"    void contextLoads() {", "    }", "", "}");
	}

	@Test
	void servletInitializerIsContributedWhenGeneratingProjectThatUsesWarPackaging() {
		ProjectDescription description = new ProjectDescription();
		description.setPackaging(new WarPackaging());
		description.setApplicationName("MyDemoApplication");
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles())
				.contains("src/main/java/com/example/demo/ServletInitializer.java");
		List<String> lines = projectStructure
				.readAllLines("src/main/java/com/example/demo/ServletInitializer.java");
		assertThat(lines).containsExactly("package com.example.demo;", "",
				"import org.springframework.boot.builder.SpringApplicationBuilder;",
				"import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;",
				"",
				"public class ServletInitializer extends SpringBootServletInitializer {",
				"", "    @Override",
				"    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {",
				"        return application.sources(MyDemoApplication.class);", "    }",
				"", "}");
	}

	@Test
	void customPackageNameIsUsedWhenGeneratingProject() {
		ProjectDescription description = new ProjectDescription();
		description.setPackageName("com.example.foo");
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles()).contains(
				"src/main/java/com/example/foo/DemoApplication.java",
				"src/test/java/com/example/foo/DemoApplicationTests.java");
	}

	@Test
	void customApplicationNameIsUsedWhenGeneratingProject() {
		ProjectDescription description = new ProjectDescription();
		description.setApplicationName("MyApplication");
		ProjectStructure projectStructure = this.projectTester.generate(description);
		assertThat(projectStructure.getRelativePathsOfProjectFiles()).contains(
				"src/main/java/com/example/demo/MyApplication.java",
				"src/test/java/com/example/demo/MyApplicationTests.java");
	}

}
