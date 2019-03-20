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
