/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web.mapper;

import java.util.ArrayList;
import java.util.List;

import io.spring.initializr.metadata.Link;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

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
		assertThat(linkModel.get("href").asString()).isEqualTo("https://example.com");
		assertThat(linkModel.get("title").asString()).isEqualTo("some description");
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
		assertThat(linkModel.get("href").asString()).isEqualTo("https://example.com/{bootVersion}/a");
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
		assertThat(linksModel.get(0).get("href").asString()).isEqualTo("https://example.com");
		assertThat(linksModel.get(1).get("href").asString()).isEqualTo("https://example.com/2");
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
