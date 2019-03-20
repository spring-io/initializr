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

package io.spring.initializr.generator.spring.code;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.SourceCodeWriter;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.util.LambdaSafe;

import org.springframework.beans.factory.ObjectProvider;

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

	private final ResolvedProjectDescription projectDescription;

	private final Supplier<S> sourceFactory;

	private final SourceCodeWriter<S> sourceWriter;

	private final ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers;

	private final ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers;

	public TestSourceCodeProjectContributor(ResolvedProjectDescription projectDescription,
			Supplier<S> sourceFactory, SourceCodeWriter<S> sourceWriter,
			ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
			ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers) {
		this.projectDescription = projectDescription;
		this.sourceFactory = sourceFactory;
		this.sourceWriter = sourceWriter;
		this.testApplicationTypeCustomizers = testApplicationTypeCustomizers;
		this.testSourceCodeCustomizers = testSourceCodeCustomizers;
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		S sourceCode = this.sourceFactory.get();
		String testName = this.projectDescription.getApplicationName() + "Tests";
		C compilationUnit = sourceCode.createCompilationUnit(
				this.projectDescription.getPackageName(), testName);
		T testApplicationType = compilationUnit.createTypeDeclaration(testName);
		customizeTestApplicationType(testApplicationType);
		customizeTestSourceCode(sourceCode);
		this.sourceWriter
				.writeTo(
						this.projectDescription.getBuildSystem().getTestDirectory(
								projectRoot, this.projectDescription.getLanguage()),
						sourceCode);
	}

	@SuppressWarnings("unchecked")
	private void customizeTestApplicationType(TypeDeclaration testApplicationType) {
		List<TestApplicationTypeCustomizer<?>> customizers = this.testApplicationTypeCustomizers
				.orderedStream().collect(Collectors.toList());
		LambdaSafe
				.callbacks(TestApplicationTypeCustomizer.class, customizers,
						testApplicationType)
				.invoke((customizer) -> customizer.customize(testApplicationType));
	}

	@SuppressWarnings("unchecked")
	private void customizeTestSourceCode(S sourceCode) {
		List<TestSourceCodeCustomizer<?, ?, ?>> customizers = this.testSourceCodeCustomizers
				.orderedStream().collect(Collectors.toList());
		LambdaSafe.callbacks(TestSourceCodeCustomizer.class, customizers, sourceCode)
				.invoke((customizer) -> customizer.customize(sourceCode));
	}

}
