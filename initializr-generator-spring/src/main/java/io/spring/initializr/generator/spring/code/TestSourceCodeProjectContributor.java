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
 * {@link ProjectContributor} for the application's test source code.
 *
 * @param <T> language-specific type declaration
 * @param <C> language-specific compilation unit
 * @param <S> language-specific source code
 * @author Stephane Nicoll
 */
public class TestSourceCodeProjectContributor<T extends TypeDeclaration, C extends CompilationUnit<T>, S extends SourceCode<T, C>>
		implements ProjectContributor {

	private final ProjectDescription description;

	private final Supplier<S> sourceFactory;

	private final SourceCodeWriter<S> sourceWriter;

	private final ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers;

	private final ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers;

	public TestSourceCodeProjectContributor(ProjectDescription description, Supplier<S> sourceFactory,
			SourceCodeWriter<S> sourceWriter,
			ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
			ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers) {
		this.description = description;
		this.sourceFactory = sourceFactory;
		this.sourceWriter = sourceWriter;
		this.testApplicationTypeCustomizers = testApplicationTypeCustomizers;
		this.testSourceCodeCustomizers = testSourceCodeCustomizers;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		S sourceCode = this.sourceFactory.get();
		String testName = this.description.getApplicationName() + "Tests";
		String packageName = this.description.getPackageName();
		Assert.state(packageName != null, "'packageName' must not be null");
		C compilationUnit = sourceCode.createCompilationUnit(packageName, testName);
		T testApplicationType = compilationUnit.createTypeDeclaration(testName);
		customizeTestApplicationType(testApplicationType);
		customizeTestSourceCode(sourceCode);
		BuildSystem buildSystem = this.description.getBuildSystem();
		Assert.state(buildSystem != null, "'buildSystem' must not be null");
		Language language = this.description.getLanguage();
		Assert.state(language != null, "'language' must not be null");
		this.sourceWriter.writeTo(buildSystem.getTestSource(projectRoot, language), sourceCode);
	}

	@SuppressWarnings("unchecked")
	private void customizeTestApplicationType(TypeDeclaration testApplicationType) {
		List<TestApplicationTypeCustomizer<?>> customizers = this.testApplicationTypeCustomizers.orderedStream()
			.toList();
		LambdaSafe.callbacks(TestApplicationTypeCustomizer.class, customizers, testApplicationType)
			.invoke((customizer) -> customizer.customize(testApplicationType));
	}

	@SuppressWarnings("unchecked")
	private void customizeTestSourceCode(S sourceCode) {
		List<TestSourceCodeCustomizer<?, ?, ?>> customizers = this.testSourceCodeCustomizers.orderedStream().toList();
		LambdaSafe.callbacks(TestSourceCodeCustomizer.class, customizers, sourceCode)
			.invoke((customizer) -> customizer.customize(sourceCode));
	}

}
