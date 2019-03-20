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

package io.spring.initializr.web.mapper;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.spring.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Link;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataJsonMapperTests {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final InitializrMetadataJsonMapper jsonMapper = new InitializrMetadataV21JsonMapper();

	@Test
	void withNoAppUrl() throws IOException {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder()
				.addType("foo", true, "/foo.zip", "none", "test")
				.addDependencyGroup("foo", "one", "two").build();
		String json = this.jsonMapper.write(metadata, null);
		JsonNode result = objectMapper.readTree(json);
		assertThat(get(result, "_links.foo.href")).isEqualTo(
				"/foo.zip?type=foo{&dependencies,packaging,javaVersion,language,bootVersion,"
						+ "groupId,artifactId,version,name,description,packageName}");
	}

	@Test
	void withAppUrl() throws IOException {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder()
				.addType("foo", true, "/foo.zip", "none", "test")
				.addDependencyGroup("foo", "one", "two").build();
		String json = this.jsonMapper.write(metadata, "http://server:8080/my-app");
		JsonNode result = objectMapper.readTree(json);
		assertThat(get(result, "_links.foo.href")).isEqualTo(
				"http://server:8080/my-app/foo.zip?type=foo{&dependencies,packaging,javaVersion,"
						+ "language,bootVersion,groupId,artifactId,version,name,description,packageName}");
	}

	@Test
	void linksRendered() {
		Dependency dependency = Dependency.withId("foo", "com.example", "foo");
		dependency.getLinks().add(Link.create("guide", "https://example.com/how-to"));
		dependency.getLinks().add(Link.create("reference", "https://example.com/doc"));
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		String json = this.jsonMapper.write(metadata, null);
		int first = json.indexOf("https://example.com/how-to");
		int second = json.indexOf("https://example.com/doc");
		// JSON objects are not ordered
		assertThat(first).isGreaterThan(0);
		assertThat(second).isGreaterThan(0);
	}

	private Object get(JsonNode result, String path) {
		String[] nodes = path.split("\\.");
		for (int i = 0; i < nodes.length - 1; i++) {
			String node = nodes[i];
			result = result.path(node);
		}
		return result.get(nodes[nodes.length - 1]).textValue();
	}

}
