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

package io.spring.initializr.web.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.spring.initializr.metadata.DefaultMetadataElement;

import org.springframework.web.client.RestTemplate;

/**
 * Reads metadata from the main spring.io website. This is a stateful service: create a
 * new instance whenever you need to refresh the content.
 *
 * @author Stephane Nicoll
 */
class SpringBootMetadataReader {

	private final JsonNode content;

	/**
	 * Parse the content of the metadata at the specified url.
	 * @param objectMapper the object mapper
	 * @param restTemplate the rest template
	 * @param url the metadata URL
	 * @throws IOException on load error
	 */
	SpringBootMetadataReader(ObjectMapper objectMapper, RestTemplate restTemplate,
			String url) throws IOException {
		this.content = objectMapper
				.readTree(restTemplate.getForObject(url, String.class));
	}

	/**
	 * Return the boot versions parsed by this instance.
	 * @return the versions
	 */
	public List<DefaultMetadataElement> getBootVersions() {
		ArrayNode releases = (ArrayNode) this.content.get("projectReleases");
		List<DefaultMetadataElement> list = new ArrayList<>();
		for (JsonNode node : releases) {
			DefaultMetadataElement version = new DefaultMetadataElement();
			version.setId(node.get("version").textValue());
			String name = node.get("versionDisplayName").textValue();
			version.setName(
					node.get("snapshot").booleanValue() ? name + " (SNAPSHOT)" : name);
			version.setDefault(node.get("current").booleanValue());
			list.add(version);
		}
		return list;
	}

}
