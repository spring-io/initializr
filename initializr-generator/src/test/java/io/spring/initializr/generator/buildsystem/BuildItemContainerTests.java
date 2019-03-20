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

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link BuildItemContainer}.
 *
 * @author Stephane Nicoll
 */
class BuildItemContainerTests {

	@Test
	void emptyContainer() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>());
		assertThat(container.isEmpty()).isTrue();
		assertThat(container.ids()).isEmpty();
		assertThat(container.items()).isEmpty();
		assertThat(container.get("any")).isNull();
		assertThat(container.has("any")).isFalse();
	}

	@Test
	void addElement() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>());
		container.add("test", "value");
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).containsOnly("value");
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.get("test")).isEqualTo("value");
		assertThat(container.has("test")).isTrue();
	}

	@Test
	void addElementWithSameIdOverrideItem() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>());
		container.add("test", "value");
		container.add("test", "another");
		assertThat(container.get("test")).isEqualTo("another");
	}

	@Test
	void addByIdWithResolution() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>(), (id) -> id.equals("test") ? "value" : null);
		container.add("test");
		assertThat(container.get("test")).isEqualTo("value");
	}

	@Test
	void addByIdWithNoResolution() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>(), (id) -> id.equals("test") ? "value" : null);
		assertThatIllegalArgumentException().isThrownBy(() -> container.add("unknown"))
				.withMessageContaining("unknown");
	}

	@Test
	void removeExistingElement() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>());
		container.add("test", "value");
		assertThat(container.remove("test")).isTrue();
		assertThat(container.ids()).isEmpty();
		assertThat(container.items()).isEmpty();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeUnknownElement() {
		BuildItemContainer<String, String> container = createTestContainer(
				new LinkedHashMap<>());
		container.add("test", "value");
		assertThat(container.remove("unknown")).isFalse();
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).containsOnly("value");
		assertThat(container.isEmpty()).isFalse();
	}

	private BuildItemContainer<String, String> createTestContainer(
			Map<String, String> content) {
		return createTestContainer(content, (id) -> null);
	}

	private BuildItemContainer<String, String> createTestContainer(
			Map<String, String> content, Function<String, String> itemResolver) {
		return new BuildItemContainer<>(content, itemResolver);
	}

}
