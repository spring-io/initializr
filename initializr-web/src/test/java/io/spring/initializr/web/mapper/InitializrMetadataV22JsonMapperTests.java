/*
 * Copyright 2012-2023 the original author or authors.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import org.junit.jupiter.api.Test;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrMetadataV22JsonMapper}.
 *
 * @author Stephane Nicoll
 */
class InitializrMetadataV22JsonMapperTests {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final InitializrMetadataV22JsonMapper jsonMapper = new InitializrMetadataV22JsonMapper();

	@Test
	void versionRangesUsingSemVerIsNotChanged() {
		Dependency dependency = Dependency.withId("test");
		dependency.setCompatibilityRange("[1.1.1-RC1,1.2.0-M1)");
		dependency.resolve();
		ObjectNode node = this.jsonMapper.mapDependency(dependency);
		assertThat(node.get("versionRange").textValue()).isEqualTo("[1.1.1-RC1,1.2.0-M1)");
	}

	@Test
	void versionRangesUsingSemVerSnapshotIsNotChanged() {
		Dependency dependency = Dependency.withId("test");
		dependency.setCompatibilityRange("1.2.0-SNAPSHOT");
		dependency.resolve();
		ObjectNode node = this.jsonMapper.mapDependency(dependency);
		assertThat(node.get("versionRange").textValue()).isEqualTo("1.2.0-SNAPSHOT");
	}

	@Test
	void platformVersionUsingSemVerUIsNotChanged() throws JsonProcessingException {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder().addBootVersion("2.5.0-SNAPSHOT", false)
			.addBootVersion("2.5.0-M2", false)
			.addBootVersion("2.4.2", true)
			.build();
		String json = this.jsonMapper.write(metadata, null);
		JsonNode result = objectMapper.readTree(json);
		JsonNode platformVersions = result.get("bootVersion");
		assertThat(platformVersions.get("default").textValue()).isEqualTo("2.4.2");
		JsonNode versions = platformVersions.get("values");
		assertThat(versions).hasSize(3);
		assertVersionMetadata(versions.get(0), "2.5.0-SNAPSHOT", "2.5.0-SNAPSHOT");
		assertVersionMetadata(versions.get(1), "2.5.0-M2", "2.5.0-M2");
		assertVersionMetadata(versions.get(2), "2.4.2", "2.4.2");
	}

	@Test
	void shouldAllowCustomization() throws JsonProcessingException {
		InitializrMetadataJsonMapper mapper = new InitializrMetadataV22JsonMapper() {
			@Override
			protected TemplateVariables getTemplateVariables(Type type) {
				TemplateVariables templateVariables = super.getTemplateVariables(type);
				return templateVariables.concat(TemplateVariable.requestParameter("testParameter"));
			}

			@Override
			protected void customizeParent(ObjectNode parent, InitializrMetadata metadata) {
				parent.put("testField", "testValue");
			}
		};
		String json = mapper.write(
				new InitializrMetadataTestBuilder().addType("id", true, "action", "build", "dialect", "format").build(),
				"http://localhost");
		JsonNode result = objectMapper.readTree(json);
		assertThat(result.get("testField").asText()).isEqualTo("testValue");
		assertThat(result.get("_links").get("id").get("href").asText()).isEqualTo(
				"http://localhost/action?type=id{&dependencies,packaging,javaVersion,language,bootVersion,groupId,artifactId,version,name,description,packageName,testParameter}");
	}

	private void assertVersionMetadata(JsonNode node, String id, String name) {
		assertThat(node.get("id").textValue()).isEqualTo(id);
		assertThat(node.get("name").textValue()).isEqualTo(name);
	}

}
