/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A container for {@linkplain GradleExtension Gradle extensions}.
 *
 * @author Moritz Halbritter
 */
public class GradleExtensionContainer {

	private final Map<String, GradleExtension.Builder> extensions = new LinkedHashMap<>();

	/**
	 * Return the {@link GradleExtension Gradle extensions} to customize.
	 * @return the gradle extensions
	 */
	public Stream<GradleExtension> values() {
		return this.extensions.values().stream().map(GradleExtension.Builder::build);
	}

	/**
	 * Customize an extension with the specified name. If the extension has already been
	 * customized, the consumer can be used to further tune the existing extension.
	 * @param name the name of the extension
	 * @param extension a callback to customize the extension
	 */
	public void customize(String name, Consumer<GradleExtension.Builder> extension) {
		extension.accept(this.extensions.computeIfAbsent(name, GradleExtension.Builder::new));
	}

	/**
	 * Return the fully qualified name of types to import.
	 * @return the imported types
	 */
	public Stream<String> importedTypes() {
		Set<String> result = new HashSet<>();
		values().forEach((extension) -> result.addAll(extension.getImportedTypes()));
		return result.stream();
	}

}
