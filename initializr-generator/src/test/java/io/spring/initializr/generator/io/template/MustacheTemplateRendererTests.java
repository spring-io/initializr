/*
 * Copyright 2012 - present the original author or authors.
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
		MustacheTemplateRenderer render = new MustacheTemplateRenderer("classpath:/templates/mustache",
				this.templatesCache);
		assertThat(this.templatesCache.get("classpath:/templates/mustache/test")).isNull();
		assertThat(render.render("test", Collections.singletonMap("key", "value"))).isEqualTo("value");
		assertThat(this.templatesCache.get("classpath:/templates/mustache/test")).isNotNull();
	}

	@Test
	void renderTemplateWithoutCache() throws IOException {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer("classpath:/templates/mustache");
		assertThat(render.render("test", Collections.singletonMap("key", "value"))).isEqualTo("value");
	}

	@Test
	void renderUnknownTemplate() {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer("classpath:/templates/mustache",
				this.templatesCache);
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> render.render("does-not-exist", Collections.emptyMap()))
			.withMessageContaining("Cannot load template")
			.withMessageContaining("does-not-exist");
	}

	@Test
	void htmlEscapingIsDisabled() throws IOException {
		MustacheTemplateRenderer render = new MustacheTemplateRenderer("classpath:/templates/mustache",
				this.templatesCache);
		assertThat(this.templatesCache.get("classpath:/templates/mustache/test")).isNull();
		assertThat(render.render("test", Collections.singletonMap("key", "it's a `<div>`")))
			.isEqualTo("it's a `<div>`");
	}

}
