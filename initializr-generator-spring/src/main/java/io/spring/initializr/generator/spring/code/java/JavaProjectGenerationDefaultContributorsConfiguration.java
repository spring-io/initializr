/*
 * Copyright 2012-2021 the original author or authors.
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

package io.spring.initializr.generator.spring.code.java;

import java.lang.reflect.Modifier;

import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.java.JavaExpressionStatement;
import io.spring.initializr.generator.language.java.JavaMethodDeclaration;
import io.spring.initializr.generator.language.java.JavaMethodInvocation;
import io.spring.initializr.generator.language.java.JavaReturnStatement;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.ServletInitializerCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Default Java language contributors.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@Configuration
class JavaProjectGenerationDefaultContributorsConfiguration {

	@Bean
	MainApplicationTypeCustomizer<JavaTypeDeclaration> mainMethodContributor() {
		return (typeDeclaration) -> {
			typeDeclaration.modifiers(Modifier.PUBLIC);
			typeDeclaration.addMethodDeclaration(
					JavaMethodDeclaration.method("main").modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
							.parameters(new Parameter("java.lang.String[]", "args"))
							.body(new JavaExpressionStatement(
									new JavaMethodInvocation("org.springframework.boot.SpringApplication", "run",
											typeDeclaration.getName() + ".class", "args"))));
		};
	}

	@Bean
	TestApplicationTypeCustomizer<JavaTypeDeclaration> junitJupiterTestMethodContributor() {
		return (typeDeclaration) -> {
			JavaMethodDeclaration method = JavaMethodDeclaration.method("contextLoads").returning("void").body();
			method.annotate(Annotation.name("org.junit.jupiter.api.Test"));
			typeDeclaration.addMethodDeclaration(method);
		};
	}

	/**
	 * Java source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		@Bean
		ServletInitializerCustomizer<JavaTypeDeclaration> javaServletInitializerCustomizer(
				ProjectDescription description) {
			return (typeDeclaration) -> {
				typeDeclaration.modifiers(Modifier.PUBLIC);
				JavaMethodDeclaration configure = JavaMethodDeclaration.method("configure")
						.modifiers(Modifier.PROTECTED)
						.returning("org.springframework.boot.builder.SpringApplicationBuilder")
						.parameters(new Parameter("org.springframework.boot.builder.SpringApplicationBuilder",
								"application"))
						.body(new JavaReturnStatement(new JavaMethodInvocation("application", "sources",
								description.getApplicationName() + ".class")));
				configure.annotate(Annotation.name("java.lang.Override"));
				typeDeclaration.addMethodDeclaration(configure);
			};
		}

	}

}
