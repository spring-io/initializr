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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=string");
	}

	@Test
	void stringPropertyDiffValuesWithSameKey() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "string");
		properties.add("test", "string2");
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=string, string2");
	}

	@Test
	void stringPropertySameValuesWithSameKey() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("test", "string");
		properties.add("test", "string");
		String written = writeProperties(properties);
		assertThat(written).isEqualToIgnoringNewLines("test=string");
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
	void writeYamlSameKeyDistinctValues() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.docker.compose.file", "./ollama/docker-compose.yml");
		properties.add("spring.docker.compose.file", "./qdrant/docker-compose.yml");
		String written = writeYaml(properties);
		assertThat(written).isEqualToNormalizingNewlines("""
				spring:
				  docker:
				    compose:
				      file:
				        - ./ollama/docker-compose.yml
				        - ./qdrant/docker-compose.yml
				""");
	}

	@Test
	void writeYamlSameKeySameValues() {
		ApplicationProperties properties = new ApplicationProperties();
		properties.add("spring.docker.compose.file", "./ollama/docker-compose.yml");
		properties.add("spring.docker.compose.file", "./ollama/docker-compose.yml");
		String written = writeYaml(properties);
		assertThat(written).isEqualToNormalizingNewlines("""
				spring:
				  docker:
				    compose:
				      file: ./ollama/docker-compose.yml
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
