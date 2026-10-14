/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.code.java;

import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.java.JavaCompilationUnit;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.language.java.JavaSourceCodeWriter;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.MainCompilationUnitCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeProjectContributor;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.TestSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.TestSourceCodeProjectContributor;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Configuration for contributions specific to the generation of a project that will use
 * Java as its language.
 *
 * @author Andy Wilkinson
 */
@ProjectGenerationConfiguration
@ConditionalOnLanguage(JavaLanguage.ID)
@Import(JavaProjectGenerationDefaultContributorsConfiguration.class)
public class JavaProjectGenerationConfiguration {

	private final ProjectDescription description;

	public JavaProjectGenerationConfiguration(ProjectDescription description) {
		this.description = description;
	}

	@Bean
	JavaSourceCodeWriter javaSourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
		return new JavaSourceCodeWriter(indentingWriterFactory);
	}

	@Bean
	MainSourceCodeProjectContributor<JavaTypeDeclaration, JavaCompilationUnit, JavaSourceCode> mainJavaSourceCodeProjectContributor(
			ObjectProvider<MainApplicationTypeCustomizer<?>> mainApplicationTypeCustomizers,
			ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers,
			ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers,
			JavaSourceCodeWriter javaSourceCodeWriter) {
		return new MainSourceCodeProjectContributor<>(this.description, JavaSourceCode::new, javaSourceCodeWriter,
				mainApplicationTypeCustomizers, mainCompilationUnitCustomizers, mainSourceCodeCustomizers);
	}

	@Bean
	TestSourceCodeProjectContributor<JavaTypeDeclaration, JavaCompilationUnit, JavaSourceCode> testJavaSourceCodeProjectContributor(
			ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
			ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers,
			JavaSourceCodeWriter javaSourceCodeWriter) {
		return new TestSourceCodeProjectContributor<>(this.description, JavaSourceCode::new, javaSourceCodeWriter,
				testApplicationTypeCustomizers, testSourceCodeCustomizers);
	}

}
