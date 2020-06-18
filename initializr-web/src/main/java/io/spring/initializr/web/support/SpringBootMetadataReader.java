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
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.Version.Qualifier;
import io.spring.initializr.generator.version.VersionParser;
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
	SpringBootMetadataReader(ObjectMapper objectMapper, RestTemplate restTemplate, String url) throws IOException {
		this.content = objectMapper.readTree(restTemplate.getForObject(url, String.class));
	}

	/**
	 * Return the boot versions parsed by this instance.
	 * @return the versions
	 */
	List<DefaultMetadataElement> getBootVersions() {
		ArrayNode releases = (ArrayNode) this.content.get("projectReleases");
		List<DefaultMetadataElement> list = new ArrayList<>();
		for (JsonNode node : releases) {
			DefaultMetadataElement versionMetadata = parseVersionMetadata(node);
			if (versionMetadata != null) {
				list.add(versionMetadata);
			}
		}
		return list;
	}

	private DefaultMetadataElement parseVersionMetadata(JsonNode node) {
		String versionId = node.get("version").textValue();
		Version version = VersionParser.DEFAULT.safeParse(versionId);
		if (version == null) {
			return null;
		}
		DefaultMetadataElement versionMetadata = new DefaultMetadataElement();
		versionMetadata.setId(versionId);
		versionMetadata.setName(determineDisplayName(version));
		versionMetadata.setDefault(node.get("current").booleanValue());
		return versionMetadata;
	}

	private String determineDisplayName(Version version) {
		StringBuilder sb = new StringBuilder();
		sb.append(version.getMajor()).append(".").append(version.getMinor()).append(".").append(version.getPatch());
		if (version.getQualifier() != null) {
			sb.append(determineSuffix(version.getQualifier()));
		}
		return sb.toString();
	}

	private String determineSuffix(Qualifier qualifier) {
		String id = qualifier.getId();
		if (id.equals("RELEASE")) {
			return "";
		}
		StringBuilder sb = new StringBuilder(" (");
		if (id.contains("SNAPSHOT")) {
			sb.append("SNAPSHOT");
		}
		else {
			sb.append(id);
			if (qualifier.getVersion() != null) {
				sb.append(qualifier.getVersion());
			}
		}
		sb.append(")");
		return sb.toString();
	}

}
