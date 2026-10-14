/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code;

import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.language.ClassName;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

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
		ServletInitializerContributor boot20ServletInitializerContributor(
				ObjectProvider<ServletInitializerCustomizer<?>> servletInitializerCustomizers) {
			String packageName = this.description.getPackageName();
			Assert.state(packageName != null, "'packageName' must not be null");
			return new ServletInitializerContributor(packageName,
					"org.springframework.boot.web.servlet.support.SpringBootServletInitializer",
					servletInitializerCustomizers);
		}

	}

}
