/*
 * Copyright 2012 - present the original author or authors.
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
