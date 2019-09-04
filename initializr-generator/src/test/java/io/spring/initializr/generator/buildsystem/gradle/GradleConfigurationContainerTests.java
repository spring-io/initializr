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

package io.spring.initializr.generator.buildsystem.gradle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link GradleConfigurationContainer}.
 *
 * @author Stephane Nicoll
 */
class GradleConfigurationContainerTests {

	@Test
	void isEmptyWithEmptyContainer() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void isEmptyWithRegisteredName() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void isEmptyWithCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.isEmpty()).isFalse();
	}

	@Test
	void hasWithMatchingRegisteredName() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		assertThat(container.has("devOnly")).isTrue();
	}

	@Test
	void hasWithMatchingCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.has("runtime")).isTrue();
	}

	@Test
	void hasWithNonMatchingNameOrCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.has("test")).isFalse();
	}

	@Test
	void removeWithMatchingName() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("com.example");
		assertThat(container.remove("com.example")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithMatchingCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.remove("runtime")).isTrue();
		assertThat(container.isEmpty()).isTrue();
	}

	@Test
	void removeWithNonMatchingNameOrCustomization() {
		GradleConfigurationContainer container = new GradleConfigurationContainer();
		container.add("devOnly");
		container.customize("runtime", (configuration) -> configuration.extendsFrom("test"));
		assertThat(container.remove("test")).isFalse();
		assertThat(container.isEmpty()).isFalse();
	}

}
