/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io.text;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import com.samskivert.mustache.MustacheException;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link MustacheSection}.
 *
 * @author Stephane Nicoll
 */
class MustacheSectionTests {

	private final MustacheTemplateRenderer renderer = new MustacheTemplateRenderer("classpath:/templates/mustache");

	@Test
	void renderSection() throws IOException {
		MustacheSection section = new MustacheSection(this.renderer, "test", Collections.singletonMap("key", "hello"));
		StringWriter writer = new StringWriter();
		section.write(new PrintWriter(writer));
		assertThat(writer.toString()).isEqualTo("hello%n".formatted());
	}

	@Test
	void renderSectionWithMissingKey() {
		MustacheSection section = new MustacheSection(this.renderer, "test",
				Collections.singletonMap("another", "hello"));
		assertThatThrownBy(() -> section.write(new PrintWriter(new StringWriter())))
			.isInstanceOf(MustacheException.class)
			.hasMessageContaining("key");
	}

	@Test
	void renderSectionWithCustomModelResolution() throws IOException {
		MustacheSection section = new MustacheSection(this.renderer, "test", Collections.emptyMap()) {
			@Override
			protected Map<String, Object> resolveModel(Map<String, Object> model) {
				return Collections.singletonMap("key", "custom");
			}
		};
		StringWriter writer = new StringWriter();
		section.write(new PrintWriter(writer));
		assertThat(writer.toString()).isEqualTo("custom%n".formatted());
	}

}
