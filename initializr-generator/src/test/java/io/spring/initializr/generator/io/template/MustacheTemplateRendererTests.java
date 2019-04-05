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
import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Tests for {@link MustacheTemplateRenderer}.
 *
 * @author Stephane Nicoll
 */
class MustacheTemplateRendererTests {

	private final Cache templatesCache = new ConcurrentMapCache("test");

	@Test
	void renderTemplate() throws IOException {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer(
				"classpath:/templates/mustache", this.templatesCache);
		assertThat(this.templatesCache.get("classpath:/templates/mustache/test"))
				.isNull();
		assertThat(render.render("test", Collections.singletonMap("key", "value")))
				.isEqualTo("value");
		assertThat(this.templatesCache.get("classpath:/templates/mustache/test"))
				.isNotNull();
	}

	@Test
	void renderTemplateWithoutCache() throws IOException {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer(
				"classpath:/templates/mustache");
		assertThat(render.render("test", Collections.singletonMap("key", "value")))
				.isEqualTo("value");
	}

	@Test
	void renderUnknownTemplate() {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer(
				"classpath:/templates/mustache", this.templatesCache);
		assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> render.render("does-not-exist", Collections.emptyMap()))
				.withMessageContaining("Cannot load template")
				.withMessageContaining("does-not-exist");
	}

	@Test
	void htmlEscapingIsDisabled() throws IOException {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer(
				"classpath:/templates/mustache", this.templatesCache);
		assertThat(this.templatesCache.get("classpath:/templates/mustache/test"))
				.isNull();
		assertThat(
				render.render("test", Collections.singletonMap("key", "it's a `<div>`")))
						.isEqualTo("it's a `<div>`");
	}

}
