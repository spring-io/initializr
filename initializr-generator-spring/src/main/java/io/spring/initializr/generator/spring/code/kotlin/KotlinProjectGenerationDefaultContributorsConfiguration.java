/*
 * Copyright 2012-2024 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.language.ClassName;
import io.spring.initializr.generator.language.CodeBlock;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.kotlin.KotlinCompilationUnit;
import io.spring.initializr.generator.language.kotlin.KotlinFunctionDeclaration;
import io.spring.initializr.generator.language.kotlin.KotlinModifier;
import io.spring.initializr.generator.language.kotlin.KotlinTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.code.MainCompilationUnitCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default Kotlin language contributors.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 * @author Moritz Halbritter
 * @author Sijun Yang
 */
@Configuration
class KotlinProjectGenerationDefaultContributorsConfiguration {

	@Bean
	TestApplicationTypeCustomizer<KotlinTypeDeclaration> junitJupiterTestMethodContributor() {
		return (typeDeclaration) -> {
			KotlinFunctionDeclaration function = KotlinFunctionDeclaration.function("contextLoads")
				.body(CodeBlock.of(""));
			function.annotations().add(ClassName.of("org.junit.jupiter.api.Test"));
			typeDeclaration.addFunctionDeclaration(function);
		};
	}

	@Bean
	BuildCustomizer<Build> kotlinDependenciesConfigurer() {
		return new KotlinDependenciesConfigurer();
	}

	@Bean
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID, dialect = GradleBuildSystem.DIALECT_KOTLIN)
	KotlinJpaGradleBuildCustomizer kotlinJpaGradleBuildCustomizerKotlinDsl(InitializrMetadata metadata,
			KotlinProjectSettings settings, ProjectDescription projectDescription) {
		return new KotlinJpaGradleBuildCustomizer(metadata, settings, projectDescription, '\"');
	}

	@Bean
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID, dialect = GradleBuildSystem.DIALECT_GROOVY)
	KotlinJpaGradleBuildCustomizer kotlinJpaGradleBuildCustomizerGroovyDsl(InitializrMetadata metadata,
			KotlinProjectSettings settings, ProjectDescription projectDescription) {
		return new KotlinJpaGradleBuildCustomizer(metadata, settings, projectDescription, '\'');
	}

	@Bean
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	KotlinJpaMavenBuildCustomizer kotlinJpaMavenBuildCustomizer(InitializrMetadata metadata,
			ProjectDescription projectDescription) {
		return new KotlinJpaMavenBuildCustomizer(metadata, projectDescription);
	}

	@Bean
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID, dialect = GradleBuildSystem.DIALECT_KOTLIN)
	KotlinGradleBuildCustomizer kotlinBuildCustomizerKotlinDsl(KotlinProjectSettings kotlinProjectSettings) {
		return new KotlinGradleBuildCustomizer(kotlinProjectSettings, '\"');
	}

	@Bean
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID, dialect = GradleBuildSystem.DIALECT_GROOVY)
	KotlinGradleBuildCustomizer kotlinBuildCustomizerGroovyDsl(KotlinProjectSettings kotlinProjectSettings) {
		return new KotlinGradleBuildCustomizer(kotlinProjectSettings, '\'');
	}

	/**
	 * Configuration for Kotlin projects using Spring Boot 2.0 and later.
	 */
	@Configuration
	@ConditionalOnPlatformVersion("2.0.0.M1")
	static class SpringBoot2AndLaterKotlinProjectGenerationConfiguration {

		@Bean
		@ConditionalOnBuildSystem(MavenBuildSystem.ID)
		KotlinMavenBuildCustomizer kotlinBuildCustomizer(KotlinProjectSettings kotlinProjectSettings) {
			return new KotlinMavenBuildCustomizer(kotlinProjectSettings);
		}

		@Bean
		MainCompilationUnitCustomizer<KotlinTypeDeclaration, KotlinCompilationUnit> mainFunctionContributor(
				ProjectDescription description) {
			return (compilationUnit) -> compilationUnit.addTopLevelFunction(KotlinFunctionDeclaration.function("main")
				.parameters(Parameter.of("args", "Array<String>"))
				.body(CodeBlock.ofStatement("$T<$L>(*args)", "org.springframework.boot.runApplication",
						description.getApplicationName())));
		}

	}

	/**
	 * Kotlin source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		@Bean
		ServletInitializerCustomizer<KotlinTypeDeclaration> javaServletInitializerCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				KotlinFunctionDeclaration configure = KotlinFunctionDeclaration.function("configure")
					.modifiers(KotlinModifier.OVERRIDE)
					.returning("org.springframework.boot.builder.SpringApplicationBuilder")
					.parameters(
							Parameter.of("application", "org.springframework.boot.builder.SpringApplicationBuilder"))
					.body(CodeBlock.ofStatement("return application.sources($L::class.java)",
							description.getApplicationName()));
				typeDeclaration.addFunctionDeclaration(configure);
			};
		}

	}

}
