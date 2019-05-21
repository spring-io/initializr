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

package io.spring.initializr.generator.spring.code.groovy;

import java.lang.reflect.Modifier;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.groovy.GroovyExpressionStatement;
import io.spring.initializr.generator.language.groovy.GroovyMethodDeclaration;
import io.spring.initializr.generator.language.groovy.GroovyMethodInvocation;
import io.spring.initializr.generator.language.groovy.GroovyReturnStatement;
import io.spring.initializr.generator.language.groovy.GroovyTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default Groovy language contributors.
 *
 * @author Stephane Nicoll
 * @author Jean-Baptiste Nizet
 */
@Configuration
class GroovyProjectGenerationDefaultContributorsConfiguration {

	@Bean
	public MainApplicationTypeCustomizer<GroovyTypeDeclaration> mainMethodContributor() {
		return (typeDeclaration) -> typeDeclaration
				.addMethodDeclaration(GroovyMethodDeclaration.method("main")
						.modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
						.parameters(new Parameter("java.lang.String[]", "args"))
						.body(new GroovyExpressionStatement(new GroovyMethodInvocation(
								"org.springframework.boot.SpringApplication", "run",
								typeDeclaration.getName(), "args"))));
	}

	@Bean
	@ConditionalOnPlatformVersion("[1.5.0.RELEASE,2.2.0.M3)")
	public TestApplicationTypeCustomizer<GroovyTypeDeclaration> junit4TestMethodContributor() {
		return (typeDeclaration) -> {
			GroovyMethodDeclaration method = GroovyMethodDeclaration
					.method("contextLoads").modifiers(Modifier.PUBLIC).returning("void")
					.body();
			method.annotate(Annotation.name("org.junit.Test"));
			typeDeclaration.addMethodDeclaration(method);
		};
	}

	@Bean
	@ConditionalOnPlatformVersion("2.2.0.M3")
	public TestApplicationTypeCustomizer<GroovyTypeDeclaration> junitJupiterTestMethodContributor() {
		return (typeDeclaration) -> {
			GroovyMethodDeclaration method = GroovyMethodDeclaration
					.method("contextLoads").returning("void").body();
			method.annotate(Annotation.name("org.junit.jupiter.api.Test"));
			typeDeclaration.addMethodDeclaration(method);
		};
	}

	@Bean
	public BuildCustomizer<Build> groovyDependenciesConfigurer() {
		return new GroovyDependenciesConfigurer();
	}

	/**
	 * Groovy source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		@Bean
		public ServletInitializerCustomizer<GroovyTypeDeclaration> javaServletInitializerCustomizer(
				ResolvedProjectDescription projectDescription) {
			return (typeDeclaration) -> {
				GroovyMethodDeclaration configure = GroovyMethodDeclaration
						.method("configure").modifiers(Modifier.PROTECTED)
						.returning(
								"org.springframework.boot.builder.SpringApplicationBuilder")
						.parameters(new Parameter(
								"org.springframework.boot.builder.SpringApplicationBuilder",
								"application"))
						.body(new GroovyReturnStatement(
								new GroovyMethodInvocation("application", "sources",
										projectDescription.getApplicationName())));
				configure.annotate(Annotation.name("java.lang.Override"));
				typeDeclaration.addMethodDeclaration(configure);
			};
		}

	}

	/**
	 * Configuration for Groovy projects built with Maven.
	 */
	@Configuration
	@ConditionalOnBuildSystem(MavenBuildSystem.ID)
	static class GroovyMavenProjectConfiguration {

		@Bean
		public GroovyMavenBuildCustomizer groovyBuildCustomizer() {
			return new GroovyMavenBuildCustomizer();
		}

	}

	/**
	 * Configuration for Groovy projects built with Gradle.
	 */
	@Configuration
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	static class GroovyGradleProjectConfiguration {

		@Bean
		public GroovyGradleBuildCustomizer groovyBuildCustomizer() {
			return new GroovyGradleBuildCustomizer();
		}

	}

}
