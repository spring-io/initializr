/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.io.text;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import io.spring.initializr.generator.io.template.TemplateRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests for {@link BulletedSection}.
 *
 * @author Stephane Nicoll
 */
@ExtendWith(MockitoExtension.class)
class BulletedSectionTests {

	@Mock
	@SuppressWarnings("NullAway.Init")
	private TemplateRenderer renderer;

	@Captor
	@SuppressWarnings("NullAway.Init")
	private ArgumentCaptor<Map<String, Object>> modelCaptor;

	@Test
	void bulletedSectionEmpty() {
		assertThat(new BulletedSection<String>(this.renderer, "test").isEmpty()).isTrue();
	}

	@Test
	void bulletedSectionEmptyDoesNotInvokeRender() throws IOException {
		BulletedSection<String> section = new BulletedSection<>(this.renderer, "test");
		PrintWriter writer = mock(PrintWriter.class);
		section.write(writer);
		verifyNoMoreInteractions(writer, this.renderer);
	}

	@Test
	void bulletedSectionWithItem() {
		BulletedSection<String> section = new BulletedSection<>(this.renderer, "test");
		section.addItem("test");
		assertThat(section.isEmpty()).isFalse();
	}

	@Test
	void bulletedSectionWithDefaultItemName() throws IOException {
		given(this.renderer.render(eq("template"), any())).willReturn("output");
		BulletedSection<String> section = new BulletedSection<>(this.renderer, "template");
		section.addItem("test");
		section.write(new PrintWriter(new StringWriter()));
		verify(this.renderer).render(eq("template"), this.modelCaptor.capture());
		Map<String, Object> model = this.modelCaptor.getValue();
		assertThat(model).containsOnly(entry("items", Collections.singletonList("test")));
	}

	@Test
	void bulletedSectionWithCustomItemName() throws IOException {
		given(this.renderer.render(eq("template"), any())).willReturn("output");
		BulletedSection<String> section = new BulletedSection<>(this.renderer, "template", "elements");
		section.addItem("test");
		section.write(new PrintWriter(new StringWriter()));
		verify(this.renderer).render(eq("template"), this.modelCaptor.capture());
		Map<String, Object> model = this.modelCaptor.getValue();
		assertThat(model).containsOnly(entry("elements", Collections.singletonList("test")));
	}

}
