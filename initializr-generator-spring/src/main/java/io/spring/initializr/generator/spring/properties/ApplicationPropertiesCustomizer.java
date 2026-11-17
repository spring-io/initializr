/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
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
