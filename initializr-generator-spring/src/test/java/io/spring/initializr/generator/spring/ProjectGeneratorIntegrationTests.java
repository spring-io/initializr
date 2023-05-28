/*
 * Copyright 2012-2023 the original author or authors.
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
package io.spring.initializr.generator.spring;

import java.nio.file.Path;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ProjectGenerator;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.generator.test.project.ProjectGeneratorTester;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectGenerator} that uses all available
 * {@link ProjectGenerationConfiguration} instances.
 *
 * @author Stephane Nicoll
 */
class ProjectGeneratorIntegrationTests {

    private ProjectGeneratorTester projectTester;

    @BeforeEach
    void setup(@TempDir Path directory) {
        this.projectTester = new ProjectGeneratorTester().withDirectory(directory).withIndentingWriterFactory().withBean(InitializrMetadata.class, () -> InitializrMetadataTestBuilder.withDefaults().build());
    }

    @Test
    void customBaseDirectoryIsUsedWhenGeneratingProject() {
        MutableProjectDescription description = initProjectDescription();
        description.setBuildSystem(new MavenBuildSystem());
        description.setPlatformVersion(Version.parse("2.1.0.RELEASE"));
        description.setLanguage(new JavaLanguage());
        description.setGroupId("com.example");
        description.setBaseDirectory("test/demo-app");
        ProjectStructure project = this.projectTester.generate(description);
        assertThat(project).filePaths().containsOnly("test/demo-app/.gitignore", "test/demo-app/pom.xml", "test/demo-app/mvnw", "test/demo-app/mvnw.cmd", "test/demo-app/.mvn/wrapper/maven-wrapper.properties", "test/demo-app/.mvn/wrapper/maven-wrapper.jar", "test/demo-app/src/main/java/com/example/demo/DemoApplication.java", "test/demo-app/src/main/resources/application.properties", "test/demo-app/src/test/java/com/example/demo/DemoApplicationTests.java");
    }

    private MutableProjectDescription initProjectDescription() {
        MutableProjectDescription description = new MutableProjectDescription();
        description.setApplicationName("DemoApplication");
        return description;
    }
}
