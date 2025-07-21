/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.generator.spring.code;

import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.language.ClassName;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project generation configuration for projects written in any language.
 *
 * @author Andy Wilkinson
 */
@ProjectGenerationConfiguration
public class SourceCodeProjectGenerationConfiguration {

	@Bean
	public MainApplicationTypeCustomizer<TypeDeclaration> springBootApplicationAnnotator() {
		return (typeDeclaration) -> typeDeclaration.annotations()
			.addSingle(ClassName.of("org.springframework.boot.autoconfigure.SpringBootApplication"));
	}

	@Bean
	public TestApplicationTypeCustomizer<TypeDeclaration> junitJupiterSpringBootTestTypeCustomizer() {
		return (typeDeclaration) -> typeDeclaration.annotations()
			.addSingle(ClassName.of("org.springframework.boot.test.context.SpringBootTest"));
	}

	/**
	 * Language-agnostic source code contributions for projects using war packaging.
	 */
	@Configuration
	@ConditionalOnPackaging(WarPackaging.ID)
	static class WarPackagingConfiguration {

		private final ProjectDescription description;

		WarPackagingConfiguration(ProjectDescription description) {
			this.description = description;
		}

		@Bean
		@ConditionalOnPlatformVersion("2.0.0.M1")
		ServletInitializerContributor boot20ServletInitializerContributor(
				ObjectProvider<ServletInitializerCustomizer<?>> servletInitializerCustomizers) {
			return new ServletInitializerContributor(this.description.getPackageName(),
					"org.springframework.boot.web.servlet.support.SpringBootServletInitializer",
					servletInitializerCustomizers);
		}

	}

}
