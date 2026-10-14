/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language;

import org.jspecify.annotations.Nullable;

/**
 * A factory for creating a {@link Language}.
 *
 * @author Andy Wilkinson
 */
public interface LanguageFactory {

	/**
	 * Creates and returns a {@link Language} for the given id and JVM version. If the
	 * factory does not recognize the given {@code id}, {@code null} should be returned.
	 * @param id the id of the language
	 * @param jvmVersion the jvm version or {@code null} to use the default
	 * @return the language or {@code null}
	 */
	@Nullable Language createLanguage(@Nullable String id, @Nullable String jvmVersion);

}
