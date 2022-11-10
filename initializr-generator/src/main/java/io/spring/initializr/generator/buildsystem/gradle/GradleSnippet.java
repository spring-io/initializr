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
