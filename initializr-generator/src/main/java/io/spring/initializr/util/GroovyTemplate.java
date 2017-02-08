/*
 * Copyright 2012-2017 the original author or authors.
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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StreamUtils;

import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;

/**
 * @author Dave Syer
 */
public class GroovyTemplate {

	private boolean cache = true;

	private final TemplateEngine engine;
	private final ConcurrentMap<String, Template> templateCaches = new ConcurrentReferenceHashMap<>();

	public GroovyTemplate(TemplateEngine engine) {
		this.engine = engine;
	}

	public GroovyTemplate() {
		this(new GStringTemplateEngine());
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public String process(String name, Map<String, ?> model) {
		try {
			Template template = getTemplate(name);
			Writable writable = template.make(model);
			StringWriter result = new StringWriter();
			writable.writeTo(result);
			return result.toString();
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot render template", e);
		}
	}

	public Template getTemplate(String name)
			throws CompilationFailedException, ClassNotFoundException, IOException {
		if (cache) {
			return this.templateCaches.computeIfAbsent(name, n -> loadTemplate(n));
		}
		return loadTemplate(name);
	}

	protected Template loadTemplate(String name) {
		try {
			File file = new File("templates", name);
			if (file.exists()) {
				return engine.createTemplate(file);
			}

			ClassLoader classLoader = GroovyTemplate.class.getClassLoader();
			URL resource = classLoader.getResource("templates/" + name);
			if (resource != null) {
				return engine.createTemplate(StreamUtils
						.copyToString(resource.openStream(), Charset.forName("UTF-8")));
			}

			return engine.createTemplate(name);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot load template " + name, e);
		}
	}

}
