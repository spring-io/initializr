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
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueRetrievalException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * A {@link TemplateRenderer} using Mustache.
 *
 * @author Stephane Nicoll
 */
public class MustacheTemplateRenderer implements TemplateRenderer {

	private final Compiler mustache;

	private final Function<String, String> keyGenerator;

	private final Cache templateCache;

	public MustacheTemplateRenderer(String resourcePrefix, Cache templateCache) {
		String prefix = (resourcePrefix.endsWith("/") ? resourcePrefix
				: resourcePrefix + "/");
		this.mustache = Mustache.compiler().withLoader(mustacheTemplateLoader(prefix))
				.escapeHTML(false);
		this.keyGenerator = (name) -> String.format("%s%s", prefix, name);
		this.templateCache = templateCache;
	}

	public MustacheTemplateRenderer(String resourcePrefix) {
		this(resourcePrefix, null);
	}

	private static TemplateLoader mustacheTemplateLoader(String prefix) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		return (name) -> {
			String location = prefix + name + ".mustache";
			return new InputStreamReader(
					resourceLoader.getResource(location).getInputStream(),
					StandardCharsets.UTF_8);
		};
	}

	@Override
	public String render(String templateName, Map<String, ?> model) throws IOException {
		Template template = getTemplate(templateName);
		return template.execute(model);
	}

	private Template getTemplate(String name) {
		try {
			if (this.templateCache != null) {
				try {
					return this.templateCache.get(this.keyGenerator.apply(name),
							() -> loadTemplate(name));
				}
				catch (ValueRetrievalException ex) {
					throw ex.getCause();
				}
			}
			return loadTemplate(name);
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Cannot load template " + name, ex);
		}
	}

	private Template loadTemplate(String name) throws Exception {
		Reader template = this.mustache.loader.getTemplate(name);
		return this.mustache.compile(template);
	}

}
