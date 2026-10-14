/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.language.kotlin;

import io.spring.initializr.generator.language.Language;
import io.spring.initializr.generator.language.LanguageFactory;
import org.jspecify.annotations.Nullable;

/**
 * A {@link LanguageFactory} for Kotlin.
 *
 * @author Stephane Nicoll
 */
class KotlinLanguageFactory implements LanguageFactory {

	@Override
	public @Nullable Language createLanguage(@Nullable String id, @Nullable String jvmVersion) {
		if (KotlinLanguage.ID.equals(id)) {
			return new KotlinLanguage(jvmVersion);
		}
		return null;
	}

}
