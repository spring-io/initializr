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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.SourceCodeWriter;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.util.LambdaSafe;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.util.Assert;

/**
 * {@link ProjectContributor} for the application's main source code.
 *
 * @param <T> language-specific type declaration
 * @param <C> language-specific compilation unit
 * @param <S> language-specific source code
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class MainSourceCodeProjectContributor<T extends TypeDeclaration, C extends CompilationUnit<T>, S extends SourceCode<T, C>>
		implements ProjectContributor {

	private final ProjectDescription description;

	private final Supplier<S> sourceFactory;

	private final SourceCodeWriter<S> sourceWriter;

	private final ObjectProvider<MainApplicationTypeCustomizer<? extends TypeDeclaration>> mainTypeCustomizers;

	private final ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers;

	private final ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers;

	public MainSourceCodeProjectContributor(ProjectDescription description, Supplier<S> sourceFactory,
			SourceCodeWriter<S> sourceWriter, ObjectProvider<MainApplicationTypeCustomizer<?>> mainTypeCustomizers,
			ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers,
			ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers) {
		this.description = description;
		this.sourceFactory = sourceFactory;
		this.sourceWriter = sourceWriter;
		this.mainTypeCustomizers = mainTypeCustomizers;
		this.mainCompilationUnitCustomizers = mainCompilationUnitCustomizers;
		this.mainSourceCodeCustomizers = mainSourceCodeCustomizers;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		S sourceCode = this.sourceFactory.get();
		String applicationName = this.description.getApplicationName();
		Assert.state(applicationName != null, "'applicationName' must not be null");
		String packageName = this.description.getPackageName();
		Assert.state(packageName != null, "'packageName' must not be null");
		C compilationUnit = sourceCode.createCompilationUnit(packageName, applicationName);
		T mainApplicationType = compilationUnit.createTypeDeclaration(applicationName);
		customizeMainApplicationType(mainApplicationType);
		customizeMainCompilationUnit(compilationUnit);
		customizeMainSourceCode(sourceCode);
		BuildSystem buildSystem = this.description.getBuildSystem();
		Assert.state(buildSystem != null, "'buildSystem' must not be null");
		Language language = this.description.getLanguage();
		Assert.state(language != null, "'language' must not be null");
		this.sourceWriter.writeTo(buildSystem.getMainSource(projectRoot, language), sourceCode);
	}

	@SuppressWarnings("unchecked")
	private void customizeMainApplicationType(T mainApplicationType) {
		List<MainApplicationTypeCustomizer<?>> customizers = this.mainTypeCustomizers.orderedStream().toList();
		LambdaSafe.callbacks(MainApplicationTypeCustomizer.class, customizers, mainApplicationType)
			.invoke((customizer) -> customizer.customize(mainApplicationType));
	}

	@SuppressWarnings("unchecked")
	private void customizeMainCompilationUnit(C compilationUnit) {
		List<MainCompilationUnitCustomizer<?, ?>> customizers = this.mainCompilationUnitCustomizers.orderedStream()
			.toList();
		LambdaSafe.callbacks(MainCompilationUnitCustomizer.class, customizers, compilationUnit)
			.invoke((customizer) -> customizer.customize(compilationUnit));
	}

	@SuppressWarnings("unchecked")
	private void customizeMainSourceCode(S sourceCode) {
		List<MainSourceCodeCustomizer<?, ?, ?>> customizers = this.mainSourceCodeCustomizers.orderedStream().toList();
		LambdaSafe.callbacks(MainSourceCodeCustomizer.class, customizers, sourceCode)
			.invoke((customizer) -> customizer.customize(sourceCode));
	}

}
