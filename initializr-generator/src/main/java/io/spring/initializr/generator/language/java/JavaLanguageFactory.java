/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.LanguageFactory;
import org.jspecify.annotations.Nullable;

/**
 * A {@link LanguageFactory} for Java.
 *
 * @author Andy Wilkinson
 */
class JavaLanguageFactory implements LanguageFactory {

	@Override
	public @Nullable Language createLanguage(@Nullable String id, @Nullable String jvmVersion) {
		if (JavaLanguage.ID.equals(id)) {
			return new JavaLanguage(jvmVersion);
		}
		return null;
	}

}
