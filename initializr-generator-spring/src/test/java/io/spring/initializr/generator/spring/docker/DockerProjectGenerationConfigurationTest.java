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
package io.spring.initializr.generator.spring.docker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.test.io.TextTestUtils;
import io.spring.initializr.generator.test.project.ProjectAssetTester;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DockerProjectGenerationConfiguration}.
 *
 * @author Taekhyun Kim
 */
public class DockerProjectGenerationConfigurationTest {

	private final ProjectAssetTester projectTester = new ProjectAssetTester()
			.withConfiguration(DockerProjectGenerationConfiguration.class);

	@Test
	void dockerFileContributor(@TempDir Path directory) {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		Path projectDirectory = this.projectTester.withDirectory(directory).generate(description, (context) -> {
			DockerFileContributor contributor = context.getBean(DockerFileContributor.class);
			contributor.contribute(directory);
			return directory;
		});
		assertThat(projectDirectory.resolve("dockerfile")).isRegularFile();
	}

	@Test
	void dockerFile() {
		MutableProjectDescription description = new MutableProjectDescription();
		description.setBuildSystem(new GradleBuildSystem());
		description.setVersion("1.0.0.RELEASE");
		description.setArtifactId("Test");
		description.setPackaging(Packaging.forId(JarPackaging.ID));
		assertThat(generateDockerfile(description)).contains("FROM openjdk:8-jdk-alpine",
				"ADD build/libs/Test-1.0.0.RELEASE.jar app.jar",
				"ENTRYPOINT exec java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILE $JAVA_OPTIONS -jar /app.jar");
	}

	private List<String> generateDockerfile(MutableProjectDescription description) {
		return this.projectTester.generate(description, (context) -> {
			DockerFile dockerFile = context.getBean(DockerFile.class);
			StringWriter out = new StringWriter();
			dockerFile.write(new PrintWriter(out));
			return TextTestUtils.readAllLines(out.toString());
		});
	}

}
