/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.buildsystem.gradle;

/**
 * A {@link GradleBuild} writer for {@code settings.gradle.kts}.
 *
 * @author Jean-Baptiste Nizet
 */
public class KotlinDslGradleSettingsWriter extends GradleSettingsWriter {

	@Override
	protected String wrapWithQuotes(String value) {
		return "\"" + value + "\"";
	}

	@Override
	protected String escape(String value) {
		return GradleDslEscaper.kotlin(value);
	}

	@Override
	protected String urlAssignment(String url) {
		return "url = uri(\"" + url + "\")";
	}

}
