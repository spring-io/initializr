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

package io.spring.initializr.web.mapper;

import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.Type;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrMetadataV22JsonMapper}.
 *
 * @author Stephane Nicoll
 */
class InitializrMetadataV22JsonMapperTests {

	private static final JsonMapper jsonMapper = JsonMapper.builder().build();

	private final InitializrMetadataV22JsonMapper mapper = new InitializrMetadataV22JsonMapper();

	@Test
	void versionRangesUsingSemVerIsNotChanged() {
		Dependency dependency = Dependency.withId("test");
		dependency.setCompatibilityRange("[1.1.1-RC1,1.2.0-M1)");
		dependency.resolve();
		ObjectNode node = this.mapper.mapDependency(dependency);
		assertThat(node.get("versionRange").asString()).isEqualTo("[1.1.1-RC1,1.2.0-M1)");
	}

	@Test
	void versionRangesUsingSemVerSnapshotIsNotChanged() {
		Dependency dependency = Dependency.withId("test");
		dependency.setCompatibilityRange("1.2.0-SNAPSHOT");
		dependency.resolve();
		ObjectNode node = this.mapper.mapDependency(dependency);
		assertThat(node.get("versionRange").asString()).isEqualTo("1.2.0-SNAPSHOT");
	}

	@Test
	void platformVersionUsingSemVerUIsNotChanged() {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder().addBootVersion("2.5.0-SNAPSHOT", false)
			.addBootVersion("2.5.0-M2", false)
			.addBootVersion("2.4.2", true)
			.build();
		String json = this.mapper.write(metadata, null);
		JsonNode result = jsonMapper.readTree(json);
		JsonNode platformVersions = result.get("bootVersion");
		assertThat(platformVersions.get("default").asString()).isEqualTo("2.4.2");
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
		JsonNode result = jsonMapper.readTree(json);
		assertThat(result.get("testField").asString()).isEqualTo("testValue");
		assertThat(result.get("_links").get("id").get("href").asString()).isEqualTo(
				"http://localhost/action?type=id{&dependencies,packaging,javaVersion,language,bootVersion,groupId,artifactId,version,name,description,packageName,testParameter}");
	}

	private void assertVersionMetadata(JsonNode node, String id, String name) {
		assertThat(node.get("id").asString()).isEqualTo(id);
		assertThat(node.get("name").asString()).isEqualTo(name);
	}

}
