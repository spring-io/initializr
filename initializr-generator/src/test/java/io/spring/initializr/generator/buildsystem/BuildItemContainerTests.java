/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.generator.buildsystem;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;
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
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>());
		assertThat(container.isEmpty()).isTrue();
		assertThat(container.ids()).isEmpty();
		assertThat(container.items()).isEmpty();
		assertThat(container.get("any")).isNull();
		assertThat(container.has("any")).isFalse();
	}

	@Test
	void addElement() {
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>());
		container.add("test", "value");
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).containsOnly("value");
		assertThat(container.isEmpty()).isFalse();
		assertThat(container.get("test")).isEqualTo("value");
		assertThat(container.has("test")).isTrue();
	}

	@Test
	void addElementWithSameIdOverrideItem() {
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>());
		container.add("test", "value");
		container.add("test", "another");
		assertThat(container.get("test")).isEqualTo("another");
	}

	@Test
	void addByIdWithResolution() {
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>(),
				(id) -> id.equals("test") ? "value" : null);
		container.add("test");
		assertThat(container.get("test")).isEqualTo("value");
	}

	@Test
	void addByIdWithNoResolution() {
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>(),
				(id) -> id.equals("test") ? "value" : null);
		assertThatIllegalArgumentException().isThrownBy(() -> container.add("unknown"))
			.withMessageContaining("unknown");
	}

	@Test
	void removeExistingElement() {
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>());
		container.add("test", "value");
		assertThat(container.remove("test")).isTrue();
		assertThat(container.ids()).isEmpty();
		assertThat(container.items()).isEmpty();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeUnknownElement() {
		BuildItemContainer<String, String> container = createTestContainer(new LinkedHashMap<>());
		container.add("test", "value");
		assertThat(container.remove("unknown")).isFalse();
		assertThat(container.ids()).containsOnly("test");
		assertThat(container.items()).containsOnly("value");
		assertThat(container.isEmpty()).isFalse();
	}

	private BuildItemContainer<String, String> createTestContainer(Map<String, String> content) {
		return createTestContainer(content, (id) -> null);
	}

	private BuildItemContainer<String, String> createTestContainer(Map<String, String> content,
			Function<String, @Nullable String> itemResolver) {
		return new BuildItemContainer<>(content, itemResolver);
	}

}
