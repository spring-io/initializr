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

package io.spring.initializr.generator.spring.build;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.Dependency.Exclusion;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.build.maven.DefaultMavenBuildCustomizer;
import io.spring.initializr.generator.spring.code.kotlin.KotlinJpaGradleBuildCustomizer;
import io.spring.initializr.generator.spring.code.kotlin.KotlinJpaMavenBuildCustomizer;
import io.spring.initializr.generator.spring.code.kotlin.KotlinProjectSettings;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.context.annotation.Bean;

/**
 * Project generation configuration for projects using any build system.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
@ProjectGenerationConfiguration
public class BuildProjectGenerationConfiguration {

	@Bean
	@ConditionalOnPlatformVersion("[1.5.0.RELEASE,2.2.0.M3)")
	public BuildCustomizer<Build> junit4TestStarterContributor() {
		return (build) -> build.dependencies().add("test", "org.springframework.boot",
				"spring-boot-starter-test", DependencyScope.TEST_COMPILE);
	}

	@Bean
	@ConditionalOnPlatformVersion("2.2.0.M3")
	public BuildCustomizer<Build> junitJupiterTestStarterContributor() {
		return (build) -> build.dependencies().add("test", Dependency
				.withCoordinates("org.springframework.boot", "spring-boot-starter-test")
				.scope(DependencyScope.TEST_COMPILE)
				.exclusions(new Exclusion("org.junit.vintage", "junit-vintage-engine"),
						new Exclusion("junit", "junit")));
	}

	@Bean
	public DefaultStarterBuildCustomizer defaultStarterContributor(
			InitializrMetadata metadata) {
		return new DefaultStarterBuildCustomizer(metadata);
	}

	@Bean
	public DefaultMavenBuildCustomizer initializrMetadataMavenBuildCustomizer(
			ResolvedProjectDescription projectDescription, InitializrMetadata metadata) {
		return new DefaultMavenBuildCustomizer(projectDescription, metadata);
	}

	@Bean
	@ConditionalOnPackaging(WarPackaging.ID)
	public WarPackagingWebStarterBuildCustomizer warPackagingWebStarterBuildCustomizer(
			InitializrMetadata metadata) {
		return new WarPackagingWebStarterBuildCustomizer(metadata);
	}

	@Bean
	@ConditionalOnLanguage(KotlinLanguage.ID)
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	public KotlinJpaGradleBuildCustomizer kotlinJpaGradleBuildCustomizer(
			InitializrMetadata metadata, KotlinProjectSettings settings) {
		return new KotlinJpaGradleBuildCustomizer(metadata, settings);
	}

	@Bean
	@ConditionalOnLanguage(KotlinLanguage.ID)
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	public KotlinJpaMavenBuildCustomizer kotlinJpaMavenBuildCustomizer(
			InitializrMetadata metadata) {
		return new KotlinJpaMavenBuildCustomizer(metadata);
	}

	@Bean
	public DependencyManagementBuildCustomizer dependencyManagementBuildCustomizer(
			ResolvedProjectDescription projectDescription, InitializrMetadata metadata) {
		return new DependencyManagementBuildCustomizer(projectDescription, metadata);
	}

	@Bean
	public SimpleBuildCustomizer projectDescriptionBuildCustomizer(
			ResolvedProjectDescription projectDescription) {
		return new SimpleBuildCustomizer(projectDescription);
	}

	@Bean
	public SpringBootVersionRepositoriesBuildCustomizer repositoriesBuilderCustomizer(
			ResolvedProjectDescription description) {
		return new SpringBootVersionRepositoriesBuildCustomizer(
				description.getPlatformVersion());
	}

}
