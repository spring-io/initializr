/*
 * Copyright and licensing information for this source code can be found in
 * the NOTICE and LICENSE files distributed with this project.
 */

package io.spring.initializr.generator.io.template;

import java.io.IOException;
import java.util.Map;

/**
 * Template rendering abstraction.
 *
 * @author Stephane Nicoll
 */
@FunctionalInterface
public interface TemplateRenderer {

	/**
	 * Render the template with the specified name and the specified model.
	 * @param templateName the name of the template
	 * @param model the model to use
	 * @return the rendering result
	 * @throws IOException if rendering the template failed
	 */
	String render(String templateName, Map<String, ?> model) throws IOException;

}
