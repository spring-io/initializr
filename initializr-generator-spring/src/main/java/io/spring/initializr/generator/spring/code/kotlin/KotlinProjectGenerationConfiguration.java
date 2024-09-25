/*
 * Copyright 2012-2023 the original author or authors.
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

import io.spring.initializr.generator.buildsystem.gradle.GradleBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnLanguage;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.kotlin.KotlinCompilationUnit;
import io.spring.initializr.generator.language.kotlin.KotlinLanguage;
import io.spring.initializr.generator.language.kotlin.KotlinSourceCode;
import io.spring.initializr.generator.language.kotlin.KotlinSourceCodeWriter;
import io.spring.initializr.generator.language.kotlin.KotlinTypeDeclaration;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.MainCompilationUnitCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.MainSourceCodeProjectContributor;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.TestSourceCodeCustomizer;
import io.spring.initializr.generator.spring.code.TestSourceCodeProjectContributor;
import io.spring.initializr.generator.spring.scm.git.GitIgnore;
import io.spring.initializr.generator.spring.scm.git.GitIgnoreCustomizer;
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
 * @author Moritz Halbritter
 */
@ProjectGenerationConfiguration
@ConditionalOnLanguage(KotlinLanguage.ID)
@Import(KotlinProjectGenerationDefaultContributorsConfiguration.class)
public class KotlinProjectGenerationConfiguration {

	private final ProjectDescription description;

	public KotlinProjectGenerationConfiguration(ProjectDescription description) {
		this.description = description;
	}

	@Bean
	KotlinSourceCodeWriter kotlinSourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
		return new KotlinSourceCodeWriter(this.description.getLanguage(), indentingWriterFactory);
	}

	@Bean
	MainSourceCodeProjectContributor<KotlinTypeDeclaration, KotlinCompilationUnit, KotlinSourceCode> mainKotlinSourceCodeProjectContributor(
			ObjectProvider<MainApplicationTypeCustomizer<?>> mainApplicationTypeCustomizers,
			ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers,
			ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers,
			KotlinSourceCodeWriter kotlinSourceCodeWriter) {
		return new MainSourceCodeProjectContributor<>(this.description, KotlinSourceCode::new, kotlinSourceCodeWriter,
				mainApplicationTypeCustomizers, mainCompilationUnitCustomizers, mainSourceCodeCustomizers);
	}

	@Bean
	TestSourceCodeProjectContributor<KotlinTypeDeclaration, KotlinCompilationUnit, KotlinSourceCode> testKotlinSourceCodeProjectContributor(
			ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
			ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers,
			KotlinSourceCodeWriter kotlinSourceCodeWriter) {
		return new TestSourceCodeProjectContributor<>(this.description, KotlinSourceCode::new, kotlinSourceCodeWriter,
				testApplicationTypeCustomizers, testSourceCodeCustomizers);
	}

	@Bean
	@ConditionalOnBuildSystem(GradleBuildSystem.ID)
	public GitIgnoreCustomizer kotlinGradlePluginGitIgnoreCustomizer() {
		return (gitIgnore) -> {
			GitIgnore.GitIgnoreSection section = gitIgnore.addSectionIfAbsent("Kotlin");
			section.add(".kotlin");
		};
	}

	@Bean
	public KotlinProjectSettings kotlinProjectSettings(ObjectProvider<KotlinVersionResolver> kotlinVersionResolver,
			InitializrMetadata metadata) {
		String kotlinVersion = kotlinVersionResolver
			.getIfAvailable(() -> new InitializrMetadataKotlinVersionResolver(metadata))
			.resolveKotlinVersion(this.description);
		return new SimpleKotlinProjectSettings(kotlinVersion, this.description.getLanguage().jvmVersion());
	}

	@Bean
	public KotlinJacksonBuildCustomizer kotlinJacksonBuildCustomizer(InitializrMetadata metadata) {
		return new KotlinJacksonBuildCustomizer(metadata, this.description);
	}

}
