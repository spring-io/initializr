/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
