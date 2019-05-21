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

package io.spring.initializr.generator.spring.build.gradle;

import java.util.List;
import java.util.stream.Collectors;

import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.gradle.Gradle3BuildWriter;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleBuildWriter;
import io.spring.initializr.generator.buildsystem.gradle.GroovyDslGradleSettingsWriter;
import io.spring.initializr.generator.buildsystem.gradle.KotlinDslGradleBuildWriter;
import io.spring.initializr.generator.buildsystem.gradle.KotlinDslGradleSettingsWriter;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for contributions specific to the generation of a project that will use
 * Gradle as its build system.
 *
 * @author Andy Wilkinson
 * @author Jean-Baptiste Nizet
 */
@ProjectGenerationConfiguration
@ConditionalOnBuildSystem(GradleBuildSystem.ID)
public class GradleProjectGenerationConfiguration {

	private final IndentingWriterFactory indentingWriterFactory;

	public GradleProjectGenerationConfiguration(
			IndentingWriterFactory indentingWriterFactory) {
		this.indentingWriterFactory = indentingWriterFactory;
	}

	@Bean
	public GradleBuild gradleBuild(ObjectProvider<BuildItemResolver> buildItemResolver,
			ObjectProvider<BuildCustomizer<?>> buildCustomizers) {
		return createGradleBuild(buildItemResolver.getIfAvailable(),
				buildCustomizers.orderedStream().collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	private GradleBuild createGradleBuild(BuildItemResolver buildItemResolver,
			List<BuildCustomizer<?>> buildCustomizers) {
		GradleBuild build = (buildItemResolver != null)
				? new GradleBuild(buildItemResolver) : new GradleBuild();
		LambdaSafe.callbacks(BuildCustomizer.class, buildCustomizers, build)
				.invoke((customizer) -> customizer.customize(build));
		return build;
	}

	@Bean
	public BuildCustomizer<GradleBuild> defaultGradleBuildCustomizer(
			ResolvedProjectDescription projectDescription) {
		return (build) -> build
				.setSourceCompatibility(projectDescription.getLanguage().jvmVersion());
	}

	@Bean
	public GradleConfigurationBuildCustomizer gradleConfigurationBuildCustomizer() {
		return new GradleConfigurationBuildCustomizer();
	}

	@Bean
	@ConditionalOnLanguage(JavaLanguage.ID)
	public BuildCustomizer<GradleBuild> javaPluginContributor() {
		return (build) -> build.addPlugin("java");
	}

	@Bean
	@ConditionalOnPackaging(WarPackaging.ID)
	public BuildCustomizer<GradleBuild> warPluginContributor() {
		return (build) -> build.addPlugin("war");
	}

	@Bean
	@ConditionalOnPlatformVersion("2.0.0.M1")
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_GROOVY)
	public BuildCustomizer<GradleBuild> applyDependencyManagementPluginContributor() {
		return (build) -> build.applyPlugin("io.spring.dependency-management");
	}

	@Bean
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_GROOVY)
	public GradleBuildProjectContributor gradleBuildProjectContributor(
			GroovyDslGradleBuildWriter buildWriter, GradleBuild build) {
		return new GradleBuildProjectContributor(buildWriter, build,
				this.indentingWriterFactory, "build.gradle");
	}

	@Bean
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_KOTLIN)
	public GradleBuildProjectContributor gradleKtsBuildProjectContributor(
			KotlinDslGradleBuildWriter buildWriter, GradleBuild build) {
		return new GradleBuildProjectContributor(buildWriter, build,
				this.indentingWriterFactory, "build.gradle.kts");
	}

	/**
	 * Configuration specific to projects using Gradle 3.
	 */
	@Configuration
	@ConditionalOnGradleVersion("3")
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	static class Gradle3ProjectGenerationConfiguration {

		@Bean
		public Gradle3BuildWriter gradleBuildWriter() {
			return new Gradle3BuildWriter();
		}

		@Bean
		public GradleWrapperContributor gradle3WrapperContributor() {
			return new GradleWrapperContributor("3");
		}

		@Bean
		public Gradle3SettingsGradleProjectContributor settingsGradleProjectContributor(
				GradleBuild build) {
			return new Gradle3SettingsGradleProjectContributor(build);
		}

		@Bean
		public BuildCustomizer<GradleBuild> springBootPluginContributor(
				ResolvedProjectDescription projectDescription) {
			return (build) -> {
				build.buildscript((buildscript) -> buildscript
						.dependency("org.springframework.boot:spring-boot-gradle-plugin:"
								+ projectDescription.getPlatformVersion()));
				build.applyPlugin("org.springframework.boot");
			};
		}

	}

	/**
	 * Configuration specific to projects using Gradle 4.
	 */
	@Configuration
	@ConditionalOnGradleVersion("4")
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	static class Gradle4ProjectGenerationConfiguration {

		@Bean
		public GradleWrapperContributor gradle4WrapperContributor() {
			return new GradleWrapperContributor("4");
		}

	}

	/**
	 * Configuration specific to projects using Gradle 5.
	 */
	@Configuration
	@ConditionalOnGradleVersion("5")
	static class Gradle5ProjectGenerationConfiguration {

		@Bean
		public GradleWrapperContributor gradle4WrapperContributor() {
			return new GradleWrapperContributor("5");
		}

	}

	/**
	 * Configuration specific to projects using Gradle (Groovy DSL) 4 or 5.
	 */
	@Configuration
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_GROOVY)
	@ConditionalOnGradleVersion({ "4", "5" })
	static class Gradle4Or5ProjectGenerationConfiguration {

		@Bean
		public GroovyDslGradleBuildWriter gradleBuildWriter() {
			return new GroovyDslGradleBuildWriter();
		}

		@Bean
		public SettingsGradleProjectContributor settingsGradleProjectContributor(
				GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
			return new SettingsGradleProjectContributor(build, indentingWriterFactory,
					new GroovyDslGradleSettingsWriter(), "settings.gradle");
		}

		@Bean
		public BuildCustomizer<GradleBuild> springBootPluginContributor(
				ResolvedProjectDescription projectDescription) {
			return (build) -> build.addPlugin("org.springframework.boot",
					projectDescription.getPlatformVersion().toString());
		}

		@Bean
		@ConditionalOnPlatformVersion("2.2.0.M3")
		public BuildCustomizer<GradleBuild> testTaskContributor() {
			return (build) -> build.customizeTask("test",
					(test) -> test.invoke("useJUnitPlatform"));
		}

		@Bean
		public GradleAnnotationProcessorScopeBuildCustomizer gradleAnnotationProcessorScopeBuildCustomizer() {
			return new GradleAnnotationProcessorScopeBuildCustomizer();
		}

	}

	/**
	 * Configuration specific to projects using Gradle (Kotlin DSL).
	 */
	@Configuration
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_KOTLIN)
	@ConditionalOnGradleVersion("5")
	static class GradleKtsProjectGenerationConfiguration {

		@Bean
		public KotlinDslGradleBuildWriter gradleKtsBuildWriter() {
			return new KotlinDslGradleBuildWriter();
		}

		@Bean
		public SettingsGradleProjectContributor settingsGradleKtsProjectContributor(
				GradleBuild build, IndentingWriterFactory indentingWriterFactory) {
			return new SettingsGradleProjectContributor(build, indentingWriterFactory,
					new KotlinDslGradleSettingsWriter(), "settings.gradle.kts");
		}

		@Bean
		public BuildCustomizer<GradleBuild> springBootPluginContributor(
				ResolvedProjectDescription projectDescription,
				InitializrMetadata metadata) {
			return (build) -> {
				build.addPlugin("org.springframework.boot",
						projectDescription.getPlatformVersion().toString());
				build.addPlugin("io.spring.dependency-management",
						metadata.getConfiguration().getEnv().getGradle()
								.getDependencyManagementPluginVersion());
			};
		}

		@Bean
		@ConditionalOnPlatformVersion("2.2.0.M3")
		public BuildCustomizer<GradleBuild> testTaskContributor() {
			return (build) -> build.customizeTasksWithType("Test",
					(test) -> test.invoke("useJUnitPlatform"));
		}

	}

}
