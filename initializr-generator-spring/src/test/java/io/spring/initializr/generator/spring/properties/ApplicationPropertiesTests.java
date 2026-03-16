/*
 * Copyright 2012 - present the original author or authors.
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
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * Tests for {@link ApplicationProperties}.
 *
 * @author Moritz Halbritter
 * @author Rodrigo Mibielli Peixoto
 */
class ApplicationPropertiesTests {

	@Test
	void getKeyFound() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "123");
		Object value = properties.get("test");
		assertThat(value).isEqualTo("123");
	}

	@Test
	void getKeyNotFound() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "123");
		Object value = properties.get("test2");
		assertThat(value).isNull();
	}

	@Test
	void getKeyFoundWCasting() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 123L);
		Long value = properties.get("test", Long.class);
		assertThat(value).isEqualTo(123L);
	}

	@Test
	void getKeyNotFoundWCasting() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 123.4);
		Double value = properties.get("test2", Double.class);
		assertThat(value).isNull();
	}

	@Test
	void getThrowsCastingException() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 123L);
		assertThatExceptionOfType(ClassCastException.class).isThrownBy(() -> properties.get("test", Integer.class));
	}

	@Test
	void containsKey() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "value");
		boolean containsKey = properties.contains("test");
		assertThat(containsKey).isTrue();
	}

	@Test
	void doesNotContainKey() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "value");
		boolean containsKey = properties.contains("test2");
		assertThat(containsKey).isFalse();
	}

	@Test
	void removesKeyFound() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "value");
		boolean removed = properties.remove("test");
		assertThat(removed).isTrue();
		assertThat(properties.contains("test")).isFalse();
	}

	@Test
	void doesNotRemoveKeyNotFound() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "value");
		boolean removed = properties.remove("test2");
		assertThat(removed).isFalse();
		assertThat(properties.contains("test")).isTrue();
	}

	@Test
	void stringProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "string");
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=string");
	}

	@Test
	void collectionProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		List<String> strings = List.of("string1", "string2");
		properties.add("test", strings);
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=string1,string2");
	}

	@Test
	void emptyCollectionProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", Collections.emptyList());
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=");
	}

	@Test
	void longProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 1);
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=1");
	}

	@Test
	void doubleProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 0.1);
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=0.1");
	}

	@Test
	void booleanProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", false);
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=false");
	}

	@Test
	void shouldFailOnExistingProperty() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", 1);
		assertThatIllegalStateException().isThrownBy(() -> properties.add("test", 2))
			.withMessage("Property 'test' already exists");
	}

	@Test
	void writeYaml() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("name", "testapp");
		properties.add("port", 8080);
		properties.add("app.version", "1.0");
		properties.add("db.host", "localhost");
		properties.add("app.config.debug", true);
		properties.add("db.connection.timeout", 30);
		String written = writeYaml(properties);
		assertThat(written).isEqualToNormalizingNewlines("""
				app:
				  config:
				    debug: true
				  version: 1.0
				port: 8080
				name: testapp
				db:
				  host: localhost
				  connection:
				    timeout: 30
				""");
	}

	@Test
	void writeYamlCollection() {
		ApplicationProperties properties = new ApplicationProperties();
		List<Integer> ints = List.of(1, 2);
		properties.add("test.sub", ints);
		String written = writeYaml(properties);
		assertThat(written).isEqualToNormalizingNewlines("""
				test:
				  sub:
				    - 1
				    - 2
				""");
	}

	@Test
	void writeEmptyYamlCollection() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test.sub", Collections.emptyList());
		String written = writeYaml(properties);
		assertThat(written).isEqualToNormalizingNewlines("""
				test:
				  sub: []
				""");
	}

	private String writeProperties(ApplicationProperties properties) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter writer = new PrintWriter(stringWriter)) {
			properties.writeProperties(writer);
		}
		return stringWriter.toString();
	}

	private String writeYaml(ApplicationProperties properties) {
		StringWriter stringWriter = new StringWriter();
		try (PrintWriter writer = new PrintWriter(stringWriter)) {
			properties.writeYaml(writer);
		}
		return stringWriter.toString();
	}

}
