/*
 * Copyright 2012-2022 the original author or authors.
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
