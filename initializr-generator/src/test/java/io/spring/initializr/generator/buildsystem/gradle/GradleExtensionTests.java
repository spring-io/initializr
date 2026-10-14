/*
 * Copyright 2012 - present the original author or authors.
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
