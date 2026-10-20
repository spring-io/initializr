/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem.gradle;

/**
 * A {@link GradleBuild} writer for {@code settings.gradle}.
 *
 * @author Jean-Baptiste Nizet
 */
public class GroovyDslGradleSettingsWriter extends GradleSettingsWriter {

	@Override
	protected String wrapWithQuotes(String value) {
		return "'" + value + "'";
	}

	@Override
	protected String escape(String value) {
		return GradleDslEscaper.groovy(value);
	}

	@Override
	protected String urlAssignment(String url) {
		return "url = " + wrapWithQuotes(url);
	}

}
