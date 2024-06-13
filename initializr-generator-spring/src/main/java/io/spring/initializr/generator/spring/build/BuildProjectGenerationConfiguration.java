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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.context.annotation.Bean;

/**
 * Project generation configuration for projects using any build system.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
public class BuildProjectGenerationConfiguration {

	@Bean
	@ConditionalOnPlatformVersion("[2.2.0.M5,2.4.0-SNAPSHOT)")
	public BuildCustomizer<Build> junit5TestStarterContributor() {
		return (build) -> build.dependencies()
			.add("test",
					Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-test")
						.scope(DependencyScope.TEST_COMPILE)
						.exclusions(new Exclusion("org.junit.vintage", "junit-vintage-engine")));
	}

	@Bean
	@ConditionalOnPlatformVersion("2.4.0-M1")
	public BuildCustomizer<Build> junitJupiterTestStarterContributor() {
		return (build) -> build.dependencies()
			.add("test", Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-test")
				.scope(DependencyScope.TEST_COMPILE));
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	BuildCustomizer<Build> junitLauncherContributor() {
		return (build) -> build.dependencies()
			.add("junit-launcher", Dependency.withCoordinates("org.junit.platform", "junit-platform-launcher")
				.scope(DependencyScope.TEST_RUNTIME));
	}

	@Bean
	public DefaultStarterBuildCustomizer defaultStarterContributor(InitializrMetadata metadata,
			ProjectDescription projectDescription) {
		return new DefaultStarterBuildCustomizer(metadata, projectDescription);
	}

	@Bean
	@ConditionalOnPackaging(WarPackaging.ID)
	public WarPackagingWebStarterBuildCustomizer warPackagingWebStarterBuildCustomizer(InitializrMetadata metadata,
			ProjectDescription projectDescription) {
		return new WarPackagingWebStarterBuildCustomizer(metadata, projectDescription);
	}

	@Bean
	public DependencyManagementBuildCustomizer dependencyManagementBuildCustomizer(ProjectDescription description,
			InitializrMetadata metadata) {
		return new DependencyManagementBuildCustomizer(description, metadata);
	}

	@Bean
	public SimpleBuildCustomizer projectDescriptionBuildCustomizer(ProjectDescription description) {
		return new SimpleBuildCustomizer(description);
	}

	@Bean
	public SpringBootVersionRepositoriesBuildCustomizer repositoriesBuilderCustomizer(ProjectDescription description) {
		return new SpringBootVersionRepositoriesBuildCustomizer(description.getPlatformVersion());
	}

}
