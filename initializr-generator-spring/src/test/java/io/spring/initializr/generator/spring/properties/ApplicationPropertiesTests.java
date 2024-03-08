/*
 * Copyright 2012-2024 the original author or authors.
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

package io.spring.initializr.generator.spring.properties;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ApplicationProperties}.
 *
 * @author Moritz Halbritter
 */
class ApplicationPropertiesTests {

	@Test
	void stringProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "string");
		String written = write(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=string");
	}

	@Test
	void longProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 1);
		String written = write(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=1");
	}

	@Test
	void doubleProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 0.1);
		String written = write(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=0.1");
	}

	@Test
	void booleanProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", false);
		String written = write(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=false");
	}

	@Test
	void shouldFailOnExistingProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 1);
		assertThatIllegalStateException().isThrownBy(() -> properties.add("test", 2))
			.withMessage("Property 'test' already exists");
	}

	private String write(ApplicationProperties properties) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter writer = new PrintWriter(stringWriter)) {
			properties.writeTo(writer);
		}
		return stringWriter.toString();
	}

}
