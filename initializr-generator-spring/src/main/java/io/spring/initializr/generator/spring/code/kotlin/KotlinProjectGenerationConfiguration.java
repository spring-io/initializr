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

import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.kotlin.KotlinCompilationUnit;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinSourceCode;
import io.spring.initializr.generator.language.kotlin.KotlinSourceCodeWriter;
import io.spring.initializr.generator.language.kotlin.KotlinTypeDeclaration;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.MainCompilationUnitCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeProjectContributor;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.TestSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.TestSourceCodeProjectContributor;
import io.spring.initializr.metadata.InitializrMetadata;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Configuration for contributions specific to the generation of a project that will use
 * Kotlin as its language.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
@ConditionalOnLanguage(KotlinLanguage.ID)
@Import(KotlinProjectGenerationDefaultContributorsConfiguration.class)
public class KotlinProjectGenerationConfiguration {

	private final ResolvedProjectDescription projectDescription;

	private final IndentingWriterFactory indentingWriterFactory;

	public KotlinProjectGenerationConfiguration(
			ResolvedProjectDescription projectDescription,
			IndentingWriterFactory indentingWriterFactory) {
		this.projectDescription = projectDescription;
		this.indentingWriterFactory = indentingWriterFactory;
	}

	@Bean
	public MainSourceCodeProjectContributor<KotlinTypeDeclaration, KotlinCompilationUnit, KotlinSourceCode> mainKotlinSourceCodeProjectContributor(
			ObjectProvider<MainApplicationTypeCustomizer<?>> mainApplicationTypeCustomizers,
			ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers,
			ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers) {
		return new MainSourceCodeProjectContributor<>(this.projectDescription,
				KotlinSourceCode::new,
				new KotlinSourceCodeWriter(this.indentingWriterFactory),
				mainApplicationTypeCustomizers, mainCompilationUnitCustomizers,
				mainSourceCodeCustomizers);
	}

	@Bean
	public TestSourceCodeProjectContributor<KotlinTypeDeclaration, KotlinCompilationUnit, KotlinSourceCode> testKotlinSourceCodeProjectContributor(
			ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
			ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers) {
		return new TestSourceCodeProjectContributor<>(this.projectDescription,
				KotlinSourceCode::new,
				new KotlinSourceCodeWriter(this.indentingWriterFactory),
				testApplicationTypeCustomizers, testSourceCodeCustomizers);
	}

	@Bean
	public KotlinProjectSettings kotlinProjectSettings(InitializrMetadata metadata) {
		return new MetadataKotlinProjectSettings(this.projectDescription, metadata);
	}

}
