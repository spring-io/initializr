/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.spring.properties;

/**
 * Callback for customizing a project's {@link ApplicationProperties}.
 *
 * @author Moritz Halbritter
 */
@FunctionalInterface
public interface ApplicationPropertiesCustomizer {

	/**
	 * Customizes the project's {@link ApplicationProperties}.
	 * @param properties the properties to customize
	 */
	void customize(ApplicationProperties properties);

}
