/*
 * Copyright 2012-2024 the original author or authors.
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

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleExtension}.
 *
 * @author Moritz Halbritter
 */
class GradleExtensionTests {

	@Test
	void shouldAddImportOnAttributes() {
		GradleExtension extension = build(
				(builder) -> builder.attributeWithType("name", "'value'", "java.lang.String"));
		assertThat(extension.getImportedTypes()).containsExactly("java.lang.String");
	}

	@Test
	void shouldAddImportOnInvoke() {
		GradleExtension extension = build((builder) -> builder.invokeWithType("set", "java.lang.String", "'value'"));
		assertThat(extension.getImportedTypes()).containsExactly("java.lang.String");
	}

	@Test
	void shouldAddImportOnAppend() {
		GradleExtension extension = build((builder) -> builder.appendWithType("name", "'value'", "java.lang.String"));
		assertThat(extension.getImportedTypes()).containsExactly("java.lang.String");
	}

	@Test
	void shouldCollectNestedImports() {
		GradleExtension extension = build((builder) -> {
			builder.importType("java.lang.String");
			builder.nested("nested", (nested) -> nested.importType("java.lang.Integer"));
		});
		assertThat(extension.getImportedTypes()).containsExactlyInAnyOrder("java.lang.String", "java.lang.Integer");
	}

	@Test
	void shouldAddImport() {
		GradleExtension extension = build((builder) -> builder.importType("java.lang.String"));
		assertThat(extension.getImportedTypes()).containsExactlyInAnyOrder("java.lang.String");
	}

	private GradleExtension build(Consumer<GradleExtension.Builder> customizer) {
		GradleExtension.Builder builder = new GradleExtension.Builder("extension");
		customizer.accept(builder);
		return builder.build();
	}

}
