/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

import java.io.IOException;

/**
 * A writer for some {@link SourceCode}.
 *
 * @param <S> the type of source code that can be written by this writer
 * @author Andy Wilkinson
 */
public interface SourceCodeWriter<S extends SourceCode<?, ?>> {

	/**
	 * Write, to the given {@code structure}, the given {@code sourceCode}.
	 * @param structure the {@link SourceStructure} beneath which the source code is
	 * written
	 * @param sourceCode the source code to write
	 * @throws IOException if writing fails
	 */
	void writeTo(SourceStructure structure, S sourceCode) throws IOException;

}
