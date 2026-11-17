/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.spring.code;

import java.util.List;

import io.spring.initializr.generator.language.CompilationUnit;
import io.spring.initializr.generator.language.SourceCode;
import io.spring.initializr.generator.language.TypeDeclaration;
import io.spring.initializr.generator.spring.util.LambdaSafe;

import org.springframework.beans.factory.ObjectProvider;

/**
 * {@link MainSourceCodeCustomizer} that contributes the servlet initializer to
 * applications using war packaging.
 *
 * @author Andy Wilkinson
 */
class ServletInitializerContributor implements
		MainSourceCodeCustomizer<TypeDeclaration, CompilationUnit<TypeDeclaration>, SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>>> {

	private final String packageName;

	private final String initializerClassName;

	private final ObjectProvider<ServletInitializerCustomizer<?>> servletInitializerCustomizers;

	ServletInitializerContributor(String packageName, String initializerClassName,
			ObjectProvider<ServletInitializerCustomizer<?>> servletInitializerCustomizers) {
		this.packageName = packageName;
		this.initializerClassName = initializerClassName;
		this.servletInitializerCustomizers = servletInitializerCustomizers;
	}

	@Override
	public void customize(SourceCode<TypeDeclaration, CompilationUnit<TypeDeclaration>> sourceCode) {
		CompilationUnit<TypeDeclaration> compilationUnit = sourceCode.createCompilationUnit(this.packageName,
				"ServletInitializer");
		TypeDeclaration servletInitializer = compilationUnit.createTypeDeclaration("ServletInitializer");
		servletInitializer.extend(this.initializerClassName);
		customizeServletInitializer(servletInitializer);
	}

	@SuppressWarnings("unchecked")
	private void customizeServletInitializer(TypeDeclaration servletInitializer) {
		List<ServletInitializerCustomizer<?>> customizers = this.servletInitializerCustomizers.orderedStream().toList();
		LambdaSafe.callbacks(ServletInitializerCustomizer.class, customizers, servletInitializer)
			.invoke((customizer) -> customizer.customize(servletInitializer));
	}

}
