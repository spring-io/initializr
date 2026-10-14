/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.java;

import javax.lang.model.SourceVersion;

import io.spring.initializr.generator.language.AbstractLanguage;
import io.spring.initializr.generator.language.Language;
import org.jspecify.annotations.Nullable;

/**
 * Java {@link Language}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public final class JavaLanguage extends AbstractLanguage {

	/**
	 * Java {@link Language} identifier.
	 */
	public static final String ID = "java";

	/**
	 * Creates a new instance with the JVM version {@value #DEFAULT_JVM_VERSION}.
	 */
	public JavaLanguage() {
		this(DEFAULT_JVM_VERSION);
	}

	/**
	 * Creates a new instance.
	 * @param jvmVersion the JVM version
	 */
	public JavaLanguage(@Nullable String jvmVersion) {
		super(ID, jvmVersion, "java");
	}

	@Override
	public boolean supportsEscapingKeywordsInPackage() {
		return false;
	}

	@Override
	public boolean isKeyword(String input) {
		return SourceVersion.isKeyword(input);
	}

}
