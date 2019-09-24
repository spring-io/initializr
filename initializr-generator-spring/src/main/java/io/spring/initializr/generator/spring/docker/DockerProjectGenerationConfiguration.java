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

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/**
 * Configuration for Docker-related contributions to a generated project.
 *
 * @author Taekhyun Kim
 */
@ProjectGenerationConfiguration
public class DockerProjectGenerationConfiguration {

	@Bean
	public DockerFileContributor dockerFileContributor(DockerFile dockerFile) {
		return new DockerFileContributor(dockerFile);
	}

	@Bean
	public DockerFile dockerFile(ObjectProvider<DockerFileCustomizer> dockerFileCustomizer) {
		DockerFile dockerFile = createDockerFile();
		dockerFileCustomizer.orderedStream().forEach((customizer) -> customizer.customize(dockerFile));
		return dockerFile;
	}

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	public DockerFileCustomizer mavenDockerFileCustomizer(ProjectDescription description) {
		return (dockerFile) -> {
			dockerFile.getAdd().add("target/" + getPackagingName(description), "app." + description.getPackaging());
			dockerFile.getEntryPoint().add("exec", "java",
					"-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILE $JAVA_OPTIONS -jar /app."
							+ description.getPackaging());
		};
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	public DockerFileCustomizer gradleDockerFileCustomizer(ProjectDescription description) {
		return (dockerFile) -> {
			dockerFile.getAdd().add("build/libs/" + getPackagingName(description), "app." + description.getPackaging());
			dockerFile.getEntryPoint().add("exec", "java",
					"-Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILE $JAVA_OPTIONS -jar /app."
							+ description.getPackaging());
		};
	}

	private DockerFile createDockerFile() {
		DockerFile dockerFile = new DockerFile();
		dockerFile.getFrom().add("openjdk:8-jdk-alpine");
		return dockerFile;
	}

	private String getPackagingName(ProjectDescription description) {
		return description.getArtifactId() + "-" + description.getVersion() + "." + description.getPackaging();
	}

}
