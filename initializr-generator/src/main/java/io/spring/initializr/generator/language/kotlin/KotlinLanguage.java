/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.kotlin;

import java.util.Set;

import io.spring.initializr.generator.language.AbstractLanguage;
import io.spring.initializr.generator.language.Language;
import org.jspecify.annotations.Nullable;

/**
 * Kotlin {@link Language}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public final class KotlinLanguage extends AbstractLanguage {

	// Taken from https://kotlinlang.org/docs/keyword-reference.html#hard-keywords
	// except keywords contains `!` or `?` because they should be handled as invalid
	// package names already
	private static final Set<String> KEYWORDS = Set.of("package", "as", "typealias", "class", "this", "super", "val",
			"var", "fun", "for", "null", "true", "false", "is", "in", "throw", "return", "break", "continue", "object",
			"if", "try", "else", "while", "do", "when", "interface", "typeof");

	/**
	 * Kotlin {@link Language} identifier.
	 */
	public static final String ID = "kotlin";

	/**
	 * Creates a new instance with the JVM version {@value #DEFAULT_JVM_VERSION}.
	 */
	public KotlinLanguage() {
		this(DEFAULT_JVM_VERSION);
	}

	/**
	 * Creates a new instance.
	 * @param jvmVersion the JVM version
	 */
	public KotlinLanguage(@Nullable String jvmVersion) {
		super(ID, jvmVersion, "kt");
	}

	@Override
	public boolean supportsEscapingKeywordsInPackage() {
		return true;
	}

	@Override
	public boolean isKeyword(String input) {
		return KEYWORDS.contains(input);
	}

}
