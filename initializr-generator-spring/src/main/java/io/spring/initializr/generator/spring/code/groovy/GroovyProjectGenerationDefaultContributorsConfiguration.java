/*
 * Copyright 2012-2022 the original author or authors.
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
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.groovy.GroovyExpressionStatement;
import io.spring.initializr.generator.language.groovy.GroovyMethodDeclaration;
import io.spring.initializr.generator.language.groovy.GroovyMethodInvocation;
import io.spring.initializr.generator.language.groovy.GroovyReturnStatement;
import io.spring.initializr.generator.language.groovy.GroovyTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

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

	private static final VersionRange GROOVY4 = VersionParser.DEFAULT.parseRange("3.0.0-M2");

	@Bean
	MainApplicationTypeCustomizer<GroovyTypeDeclaration> mainMethodContributor() {
		return (typeDeclaration) -> typeDeclaration.addMethodDeclaration(
				GroovyMethodDeclaration.method("main").modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
						.parameters(new Parameter("java.lang.String[]", "args"))
						.body(new GroovyExpressionStatement(
								new GroovyMethodInvocation("org.springframework.boot.SpringApplication", "run",
										typeDeclaration.getName(), "args"))));
	}

	@Bean
	TestApplicationTypeCustomizer<GroovyTypeDeclaration> junitJupiterTestMethodContributor() {
		return (typeDeclaration) -> {
			GroovyMethodDeclaration method = GroovyMethodDeclaration.method("contextLoads").returning("void").body();
			method.annotate(Annotation.name("org.junit.jupiter.api.Test"));
			typeDeclaration.addMethodDeclaration(method);
		};
	}

	@Bean
	BuildCustomizer<Build> groovyDependenciesConfigurer(ProjectDescription description) {
		return new GroovyDependenciesConfigurer(GROOVY4.match(description.getPlatformVersion()));
	}

	/**
	 * Groovy source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		@Bean
		ServletInitializerCustomizer<GroovyTypeDeclaration> javaServletInitializerCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				GroovyMethodDeclaration configure = GroovyMethodDeclaration.method("configure")
						.modifiers(Modifier.PROTECTED)
						.returning("org.springframework.boot.builder.SpringApplicationBuilder")
						.parameters(new Parameter("org.springframework.boot.builder.SpringApplicationBuilder",
								"application"))
						.body(new GroovyReturnStatement(new GroovyMethodInvocation("application", "sources",
								description.getApplicationName())));
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
		GroovyMavenBuildCustomizer groovyBuildCustomizer() {
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
		GroovyGradleBuildCustomizer groovyBuildCustomizer() {
			return new GroovyGradleBuildCustomizer();
		}

	}

}
