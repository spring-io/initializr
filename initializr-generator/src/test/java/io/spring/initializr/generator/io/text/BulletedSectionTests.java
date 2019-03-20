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
	private TemplateRenderer renderer;

	@Captor
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
		BulletedSection<String> section = new BulletedSection<>(this.renderer,
				"template");
		section.addItem("test");
		section.write(new PrintWriter(new StringWriter()));
		verify(this.renderer).render(eq("template"), this.modelCaptor.capture());
		Map<String, Object> model = this.modelCaptor.getValue();
		assertThat(model).containsOnly(entry("items", Collections.singletonList("test")));
	}

	@Test
	void bulletedSectionWithCustomItemName() throws IOException {
		given(this.renderer.render(eq("template"), any())).willReturn("output");
		BulletedSection<String> section = new BulletedSection<>(this.renderer, "template",
				"elements");
		section.addItem("test");
		section.write(new PrintWriter(new StringWriter()));
		verify(this.renderer).render(eq("template"), this.modelCaptor.capture());
		Map<String, Object> model = this.modelCaptor.getValue();
		assertThat(model)
				.containsOnly(entry("elements", Collections.singletonList("test")));
	}

}
