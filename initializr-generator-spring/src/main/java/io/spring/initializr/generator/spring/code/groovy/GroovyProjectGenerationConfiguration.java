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

package io.spring.initializr.generator.spring.code.groovy;

import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.groovy.GroovyCompilationUnit;
import io.spring.initializr.generator.language.groovy.GroovyLanguage;
import io.spring.initializr.generator.language.groovy.GroovySourceCode;
import io.spring.initializr.generator.language.groovy.GroovySourceCodeWriter;
import io.spring.initializr.generator.language.groovy.GroovyTypeDeclaration;
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
 * Groovy as its language.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
@ConditionalOnLanguage(GroovyLanguage.ID)
@Import(GroovyProjectGenerationDefaultContributorsConfiguration.class)
public class GroovyProjectGenerationConfiguration {

	private final ProjectDescription description;

	public GroovyProjectGenerationConfiguration(ProjectDescription description) {
		this.description = description;
	}

	@Bean
	GroovySourceCodeWriter groovySourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
		return new GroovySourceCodeWriter(indentingWriterFactory);
	}

	@Bean
	MainSourceCodeProjectContributor<GroovyTypeDeclaration, GroovyCompilationUnit, GroovySourceCode> mainGroovySourceCodeProjectContributor(
			ObjectProvider<MainApplicationTypeCustomizer<?>> mainApplicationTypeCustomizers,
			ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers,
			ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers,
			GroovySourceCodeWriter groovySourceCodeWriter) {
		return new MainSourceCodeProjectContributor<>(this.description, GroovySourceCode::new, groovySourceCodeWriter,
				mainApplicationTypeCustomizers, mainCompilationUnitCustomizers, mainSourceCodeCustomizers);
	}

	@Bean
	TestSourceCodeProjectContributor<GroovyTypeDeclaration, GroovyCompilationUnit, GroovySourceCode> testGroovySourceCodeProjectContributor(
			ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
			ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers,
			GroovySourceCodeWriter groovySourceCodeWriter) {
		return new TestSourceCodeProjectContributor<>(this.description, GroovySourceCode::new, groovySourceCodeWriter,
				testApplicationTypeCustomizers, testSourceCodeCustomizers);
	}

}
