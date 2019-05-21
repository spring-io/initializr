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

package io.spring.initializr.generator.spring.code.kotlin;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.kotlin.KotlinCompilationUnit;
import io.spring.initializr.generator.language.kotlin.KotlinExpressionStatement;
import io.spring.initializr.generator.language.kotlin.KotlinFunctionDeclaration;
import io.spring.initializr.generator.language.kotlin.KotlinFunctionInvocation;
import io.spring.initializr.generator.language.kotlin.KotlinModifier;
import io.spring.initializr.generator.language.kotlin.KotlinReifiedFunctionInvocation;
import io.spring.initializr.generator.language.kotlin.KotlinReturnStatement;
import io.spring.initializr.generator.language.kotlin.KotlinTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.code.MainCompilationUnitCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default Kotlin language contributors.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 */
@Configuration
class KotlinProjectGenerationDefaultContributorsConfiguration {

	@Bean
	@ConditionalOnPlatformVersion("[1.5.0.RELEASE,2.2.0.M3)")
	public TestApplicationTypeCustomizer<KotlinTypeDeclaration> junit4TestMethodContributor() {
		return (typeDeclaration) -> {
			KotlinFunctionDeclaration function = KotlinFunctionDeclaration
					.function("contextLoads").body();
			function.annotate(Annotation.name("org.junit.Test"));
			typeDeclaration.addFunctionDeclaration(function);
		};
	}

	@Bean
	@ConditionalOnPlatformVersion("2.2.0.M3")
	public TestApplicationTypeCustomizer<KotlinTypeDeclaration> junitJupiterTestMethodContributor() {
		return (typeDeclaration) -> {
			KotlinFunctionDeclaration function = KotlinFunctionDeclaration
					.function("contextLoads").body();
			function.annotate(Annotation.name("org.junit.jupiter.api.Test"));
			typeDeclaration.addFunctionDeclaration(function);
		};
	}

	@Bean
	public BuildCustomizer<Build> kotlinDependenciesConfigurer(
			ResolvedProjectDescription projectDescription) {
		return new KotlinDependenciesConfigurer(projectDescription.getPlatformVersion());
	}

	/**
	 * Configuration for Kotlin projects using Spring Boot 1.5.
	 */
	@Configuration
	@ConditionalOnPlatformVersion("[1.5.0.M1, 2.0.0.M1)")
	static class SpringBoot15KotlinProjectGenerationConfiguration {

		@Bean
		@ConditionalOnBuildSystem(MavenBuildSystem.ID)
		public KotlinMavenFullBuildCustomizer kotlinBuildCustomizer(
				KotlinProjectSettings kotlinProjectSettings) {
			return new KotlinMavenFullBuildCustomizer(kotlinProjectSettings);
		}

		@Bean
		public MainCompilationUnitCustomizer<KotlinTypeDeclaration, KotlinCompilationUnit> boot15MainFunctionContributor(
				ResolvedProjectDescription projectDescription) {
			return (compilationUnit) -> compilationUnit
					.addTopLevelFunction(KotlinFunctionDeclaration.function("main")
							.parameters(new Parameter("Array<String>", "args"))
							.body(new KotlinExpressionStatement(
									new KotlinFunctionInvocation(
											"org.springframework.boot.SpringApplication",
											"run", projectDescription.getApplicationName()
													+ "::class.java",
											"*args"))));
		}

	}

	/**
	 * Configuration for Kotlin projects using Spring Boot 2.0 and later.
	 */
	@Configuration
	@ConditionalOnPlatformVersion("2.0.0.M1")
	static class SpringBoot2AndLaterKotlinProjectGenerationConfiguration {

		@Bean
		@ConditionalOnBuildSystem(MavenBuildSystem.ID)
		public KotlinMavenBuildCustomizer kotlinBuildCustomizer(
				KotlinProjectSettings kotlinProjectSettings) {
			return new KotlinMavenBuildCustomizer(kotlinProjectSettings);
		}

		@Bean
		public MainCompilationUnitCustomizer<KotlinTypeDeclaration, KotlinCompilationUnit> mainFunctionContributor(
				ResolvedProjectDescription projectDescription) {
			return (compilationUnit) -> compilationUnit
					.addTopLevelFunction(KotlinFunctionDeclaration.function("main")
							.parameters(new Parameter("Array<String>", "args"))
							.body(new KotlinExpressionStatement(
									new KotlinReifiedFunctionInvocation(
											"org.springframework.boot.runApplication",
											projectDescription.getApplicationName(),
											"*args"))));
		}

	}

	/**
	 * Kotlin source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		@Bean
		public ServletInitializerCustomizer<KotlinTypeDeclaration> javaServletInitializerCustomizer(
				ResolvedProjectDescription projectDescription) {
			return (typeDeclaration) -> {
				KotlinFunctionDeclaration configure = KotlinFunctionDeclaration
						.function("configure").modifiers(KotlinModifier.OVERRIDE)
						.returning(
								"org.springframework.boot.builder.SpringApplicationBuilder")
						.parameters(new Parameter(
								"org.springframework.boot.builder.SpringApplicationBuilder",
								"application"))
						.body(new KotlinReturnStatement(
								new KotlinFunctionInvocation("application", "sources",
										projectDescription.getApplicationName()
												+ "::class.java")));
				typeDeclaration.addFunctionDeclaration(configure);
			};
		}

	}

	/**
	 * Configuration for Kotlin projects built with Gradle (Groovy DSL).
	 *
	 * @author Andy Wilkinson
	 */
	@Configuration
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_GROOVY)
	static class KotlinGradleProjectConfiguration {

		@Bean
		public KotlinGradleBuildCustomizer kotlinBuildCustomizer(
				KotlinProjectSettings kotlinProjectSettings) {
			return new GroovyDslKotlinGradleBuildCustomizer(kotlinProjectSettings);
		}

	}

	/**
	 * Configuration for Kotlin projects built with Gradle (Kotlin DSL).
	 *
	 * @author Jean-Baptiste Nizet
	 */
	@Configuration
	@ConditionalOnBuildSystem(id = GradleBuildSystem.ID,
			dialect = GradleBuildSystem.DIALECT_KOTLIN)
	static class KotlinGradleKtsProjectConfiguration {

		@Bean
		public KotlinDslKotlinGradleBuildCustomizer kotlinBuildCustomizer(
				KotlinProjectSettings kotlinProjectSettings) {
			return new KotlinDslKotlinGradleBuildCustomizer(kotlinProjectSettings);
		}

	}

}
