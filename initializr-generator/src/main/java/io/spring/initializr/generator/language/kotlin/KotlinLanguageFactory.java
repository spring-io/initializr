/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
