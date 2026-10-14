/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * A language in which a generated project can be written.
 *
 * @author Andy Wilkinson
 * @author Moritz Halbritter
 */
public interface Language {

	/**
	 * The default JVM version to use if none is specified.
	 */
	String DEFAULT_JVM_VERSION = "1.8";

	/**
	 * Return the language identifier.
	 * @return the language id
	 */
	String id();

	/**
	 * Return the JVM version level to use.
	 * @return the JVM version or {@value DEFAULT_JVM_VERSION} if not set
	 */
	String jvmVersion();

	/**
	 * Return the file extension to use for source file of this language.
	 * @return the source file extension
	 */
	String sourceFileExtension();

	/**
	 * Whether the language supports escaping keywords in package declarations.
	 * @return whether the language supports escaping keywords in package declarations.
	 */
	boolean supportsEscapingKeywordsInPackage();

	/**
	 * Whether the given {@code input} is a keyword.
	 * @param input the input
	 * @return whether the input is a keyword
	 */
	boolean isKeyword(String input);

	/**
	 * Creates the language for the given id.
	 * @param id the id
	 * @param jvmVersion the JVM version
	 * @return the language
	 * @throws IllegalStateException if no language for this id can be found
	 */
	static Language forId(@Nullable String id, @Nullable String jvmVersion) {
		return SpringFactoriesLoader.loadFactories(LanguageFactory.class, LanguageFactory.class.getClassLoader())
			.stream()
			.map((factory) -> factory.createLanguage(id, jvmVersion))
			.filter(Objects::nonNull)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Unrecognized language id '" + id + "'"));
	}

}
