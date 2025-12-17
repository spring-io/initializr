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
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import wiremock.com.fasterxml.jackson.core.JsonProcessingException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrMetadataV23JsonMapper}.
 *
 * @author Sijun Yang
 */
class InitializrMetadataV23JsonMapperTests {

	private static final JsonMapper jsonMapper = JsonMapper.builder().build();

	private final InitializrMetadataV23JsonMapper mapper = new InitializrMetadataV23JsonMapper();

	@Test
	void writeConfigurationFileFormatMetadata() throws JsonProcessingException {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder()
			.addConfigurationFileFormats("properties", true)
			.addConfigurationFileFormats("yaml", false)
			.build();
		String json = this.mapper.write(metadata, null);
		JsonNode result = jsonMapper.readTree(json);

		assertThat(result.has("configurationFileFormat")).isTrue();
		JsonNode formatsNode = result.get("configurationFileFormat");
		assertThat(formatsNode.get("type").asString()).isEqualTo("single-select");
		assertThat(formatsNode.get("default").asString()).isEqualTo("properties");
		assertThat(formatsNode.get("values")).hasSize(2);
	}

	@Test
	void writeTemplatedUriWithConfigurationFileFormatParameter() throws JsonProcessingException {
		InitializrMetadata metadata = new InitializrMetadataTestBuilder()
			.addType("id", true, "action", "build", "dialect", "format")
			.build();
		String json = this.mapper.write(metadata, null);
		JsonNode result = jsonMapper.readTree(json);

		assertThat(result.at("/_links/id/href").asString())
			.isEqualTo("/action?type=id{&dependencies,packaging,javaVersion,"
					+ "language,bootVersion,groupId,artifactId,version,name,description,packageName,configurationFileFormat}");
	}

}
