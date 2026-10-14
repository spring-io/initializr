/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io.text;

import java.io.PrintWriter;
import java.util.Map;

import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;

/**
 * {@link Section} that uses a {@link MustacheTemplateRenderer}. Renders the content with
 * a newline at the end.
 *
 * @author Madhura Bhave
 */
public class MustacheSection implements Section {

	private final MustacheTemplateRenderer templateRenderer;

	private final String templateName;

	private final Map<String, Object> model;

	/**
	 * Create a new instance.
	 * @param templateRenderer the {@link MustacheTemplateRenderer template renderer} to
	 * use
	 * @param templateName the name of the template
	 * @param model the initial model
	 */
	public MustacheSection(MustacheTemplateRenderer templateRenderer, String templateName, Map<String, Object> model) {
		this.templateRenderer = templateRenderer;
		this.templateName = templateName;
		this.model = model;
	}

	@Override
	public void write(PrintWriter writer) {
		writer.println(this.templateRenderer.render(this.templateName, resolveModel(this.model)));
	}

	/**
	 * Resolve the {@code model} prior to render the section.
	 * @param model the current model
	 * @return the model to use to render this section (never null)
	 */
	protected Map<String, Object> resolveModel(Map<String, Object> model) {
		return model;
	}

}
