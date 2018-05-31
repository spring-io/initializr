/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.util;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * A template renderer backed by Mustache.
 *
 * @author Dave Syer
 */
public class TemplateRenderer {

	private static final Logger log = LoggerFactory.getLogger(TemplateRenderer.class);

	private boolean cache = true;

	private final Compiler mustache;

	private final ConcurrentMap<String, Template> templateCaches = new ConcurrentReferenceHashMap<>();

	public TemplateRenderer() {
		this(mustacheCompiler());
	}

	public TemplateRenderer(Compiler mustache) {
		this.mustache = mustache;
	}

	public boolean isCache() {
		return this.cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public String process(String name, Map<String, ?> model) {
		try {
			Template template = getTemplate(name);
			return template.execute(model);
		}
		catch (Exception ex) {
			log.error("Cannot render: " + name, ex);
			throw new IllegalStateException("Cannot render template", ex);
		}
	}

	public Template getTemplate(String name) {
		if (this.cache) {
			return this.templateCaches.computeIfAbsent(name, this::loadTemplate);
		}
		return loadTemplate(name);
	}

	protected Template loadTemplate(String name) {
		try {
			Reader template;
			template = this.mustache.loader.getTemplate(name);
			return this.mustache.compile(template);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot load template " + name, ex);
		}
	}

	private static Compiler mustacheCompiler() {
		return Mustache.compiler().withLoader(mustacheTemplateLoader());
	}

	private static TemplateLoader mustacheTemplateLoader() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		String prefix = "classpath:/templates/";
		Charset charset = Charset.forName("UTF-8");
		return (name) -> new InputStreamReader(
				resourceLoader.getResource(prefix + name).getInputStream(), charset);
	}

}
