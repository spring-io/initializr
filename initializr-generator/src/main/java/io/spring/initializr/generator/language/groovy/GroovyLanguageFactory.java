/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.groovy;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.LanguageFactory;
import org.jspecify.annotations.Nullable;

/**
 * A {@link LanguageFactory} for Groovy.
 *
 * @author Stephane Nicoll
 */
class GroovyLanguageFactory implements LanguageFactory {

	@Override
	public @Nullable Language createLanguage(@Nullable String id, @Nullable String jvmVersion) {
		if (GroovyLanguage.ID.equals(id)) {
			return new GroovyLanguage(jvmVersion);
		}
		return null;
	}

}
