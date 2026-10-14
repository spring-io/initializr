/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.groovy;

import java.util.Set;

import io.spring.initializr.generator.language.AbstractLanguage;
import io.spring.initializr.generator.language.Language;
import org.jspecify.annotations.Nullable;

/**
 * Groovy {@link Language}.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
public final class GroovyLanguage extends AbstractLanguage {

	// See https://docs.groovy-lang.org/latest/html/documentation/#_keywords
	private static final Set<String> KEYWORDS = Set.of("abstract", "assert", "break", "case", "catch", "class", "const",
			"continue", "def", "default", "do", "else", "enum", "extends", "final", "finally", "for", "goto", "if",
			"implements", "import", "instanceof", "interface", "native", "new", "null", "non-sealed", "package",
			"public", "protected", "private", "return", "static", "strictfp", "super", "switch", "synchronized", "this",
			"threadsafe", "throw", "throws", "transient", "try", "while");

	/**
	 * Groovy {@link Language} identifier.
	 */
	public static final String ID = "groovy";

	/**
	 * Creates a new instance with the JVM version {@value #DEFAULT_JVM_VERSION}.
	 */
	public GroovyLanguage() {
		this(DEFAULT_JVM_VERSION);
	}

	/**
	 * Creates a new instance.
	 * @param jvmVersion the JVM version
	 */
	public GroovyLanguage(@Nullable String jvmVersion) {
		super(ID, jvmVersion, "groovy");
	}

	@Override
	public boolean supportsEscapingKeywordsInPackage() {
		return false;
	}

	@Override
	public boolean isKeyword(String input) {
		return KEYWORDS.contains(input);
	}

}
