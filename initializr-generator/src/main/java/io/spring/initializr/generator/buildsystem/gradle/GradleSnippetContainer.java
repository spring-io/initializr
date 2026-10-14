/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import io.spring.initializr.generator.io.IndentingWriter;

/**
 * A container for {@linkplain GradleSnippet Gradle snippets}.
 *
 * @author Stephane Nicoll
 */
public class GradleSnippetContainer {

	private final List<GradleSnippet> snippets = new ArrayList<>();

	/**
	 * Specify if this container is empty.
	 * @return {@code true} if no snippet is registered
	 */
	public boolean isEmpty() {
		return this.snippets.isEmpty();
	}

	/**
	 * Return the {@link GradleSnippet Gradle snippets} to apply.
	 * @return the gradle snippets
	 */
	public Stream<GradleSnippet> values() {
		return this.snippets.stream();
	}

	/**
	 * Return the fully qualified name of types to import.
	 * @return the imported types
	 */
	public Stream<String> importedTypes() {
		return this.snippets.stream().map(GradleSnippet::getImportedTypes).flatMap(Collection::stream);
	}

	/**
	 * Register a {@code snippet} with the specified types to import and writer.
	 * @param importedTypes the types to import
	 * @param writer the writer to use.
	 */
	public void add(Set<String> importedTypes, Consumer<IndentingWriter> writer) {
		this.snippets.add(new GradleSnippet(importedTypes, writer));
	}

	/**
	 * Register a {@code snippet} with no import.
	 * @param writer the writer to use.
	 */
	public void add(Consumer<IndentingWriter> writer) {
		add(Collections.emptySet(), writer);
	}

}
