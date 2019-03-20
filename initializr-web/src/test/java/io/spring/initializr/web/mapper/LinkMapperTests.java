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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.metadata.Link;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link LinkMapper}.
 *
 * @author Stephane Nicoll
 */
class LinkMapperTests {

	@Test
	void mapSimpleRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com", "some description"));
		ObjectNode model = LinkMapper.mapLinks(links);
		assertThat(model).hasSize(1);
		assertThat(model.has("a")).isTrue();
		ObjectNode linkModel = (ObjectNode) model.get("a");
		assertThat(linkModel).hasSize(2);
		assertThat(linkModel.get("href").textValue()).isEqualTo("https://example.com");
		assertThat(linkModel.get("title").textValue()).isEqualTo("some description");
	}

	@Test
	void mapTemplatedRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com/{bootVersion}/a", true));
		ObjectNode model = LinkMapper.mapLinks(links);
		assertThat(model).hasSize(1);
		assertThat(model.has("a")).isTrue();
		ObjectNode linkModel = (ObjectNode) model.get("a");
		assertThat(linkModel).hasSize(2);
		assertThat(linkModel.get("href").textValue())
				.isEqualTo("https://example.com/{bootVersion}/a");
		assertThat(linkModel.get("templated").booleanValue()).isEqualTo(true);
	}

	@Test
	void mergeSeveralLinksInArray() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com", "some description"));
		links.add(Link.create("a", "https://example.com/2"));
		ObjectNode model = LinkMapper.mapLinks(links);
		assertThat(model).hasSize(1);
		assertThat(model.has("a")).isTrue();
		ArrayNode linksModel = (ArrayNode) model.get("a");
		assertThat(linksModel).hasSize(2);
		assertThat(linksModel.get(0).get("href").textValue())
				.isEqualTo("https://example.com");
		assertThat(linksModel.get(1).get("href").textValue())
				.isEqualTo("https://example.com/2");
	}

	@Test
	void keepOrdering() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("first", "https://example.com"));
		links.add(Link.create("second", "https://example.com"));
		ObjectNode model = LinkMapper.mapLinks(links);
		String json = model.toString();
		assertThat(json.indexOf("first")).isLessThan(json.indexOf("second"));
	}

	@Test
	void keepOrderingWithMultipleUrlForSameRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("first", "https://example.com"));
		links.add(Link.create("second", "https://example.com"));
		links.add(Link.create("first", "https://example.com"));
		ObjectNode model = LinkMapper.mapLinks(links);
		String json = model.toString();
		assertThat(json.indexOf("first")).isLessThan(json.indexOf("second"));
	}

}
