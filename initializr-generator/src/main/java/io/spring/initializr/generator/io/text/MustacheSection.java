/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.io.text;

import java.io.IOException;
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

	public MustacheSection(MustacheTemplateRenderer templateRenderer, String templateName,
			Map<String, Object> model) {
		this.templateRenderer = templateRenderer;
		this.templateName = templateName;
		this.model = model;
	}

	@Override
	public void write(PrintWriter writer) throws IOException {
		writer.println(this.templateRenderer.render(this.templateName,
				resolveModel(this.model)));
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
