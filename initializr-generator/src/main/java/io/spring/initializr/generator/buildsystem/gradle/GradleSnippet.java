/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.Set;
import java.util.function.Consumer;

import io.spring.initializr.generator.io.IndentingWriter;

/**
 * A free-form {@code snippet} to add to a Gradle build.
 *
 * @author Stephane Nicoll
 */
public class GradleSnippet {

	private final Set<String> importedTypes;

	private final Consumer<IndentingWriter> writer;

	GradleSnippet(Set<String> importedTypes, Consumer<IndentingWriter> writer) {
		this.importedTypes = Set.copyOf(importedTypes);
		this.writer = writer;
	}

	Set<String> getImportedTypes() {
		return this.importedTypes;
	}

	/**
	 * Apply the snippet using the specified {@link IndentingWriter}.
	 * @param indentingWriter the writer to use
	 */
	public void apply(IndentingWriter indentingWriter) {
		this.writer.accept(indentingWriter);
	}

}
