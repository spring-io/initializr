/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Representation of application source code.
 *
 * @param <T> types
 * @param <C> compilation units
 * @author Andy Wilkinson
 */
public abstract class SourceCode<T extends TypeDeclaration, C extends CompilationUnit<T>> {

	private final List<C> compilationUnits = new ArrayList<>();

	private final BiFunction<String, String, C> compilationUnitFactory;

	protected SourceCode(BiFunction<String, String, C> compilationUnitFactory) {
		this.compilationUnitFactory = compilationUnitFactory;
	}

	/**
	 * Create a compilation unit and add it to this source code.
	 * @param packageName the package name
	 * @param name the name of the compilation unit
	 * @return the compilation unit
	 */
	public C createCompilationUnit(String packageName, String name) {
		C compilationUnit = this.compilationUnitFactory.apply(packageName, name);
		this.compilationUnits.add(compilationUnit);
		return compilationUnit;
	}

	/**
	 * Returns an unmodifiable view of the {@link CompilationUnit compilation units}.
	 * @return the compilation units
	 */
	public List<C> getCompilationUnits() {
		return Collections.unmodifiableList(this.compilationUnits);
	}

}
