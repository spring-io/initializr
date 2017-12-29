/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link LinkMapper}.
 *
 * @author Stephane Nicoll
 */
public class LinkMapperTests {

	@Test
	public void mapSimpleRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com", "some description"));
		ObjectNode model = LinkMapper.mapLinks(links);
		assertEquals(1, model.size());
		assertTrue(model.has("a"));
		ObjectNode linkModel = (ObjectNode) model.get("a");
		assertEquals(2, linkModel.size());
		assertEquals("https://example.com", linkModel.get("href").textValue());
		assertEquals("some description", linkModel.get("title").textValue());
	}

	@Test
	public void mapTemplatedRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com/{bootVersion}/a", true));
		ObjectNode model = LinkMapper.mapLinks(links);
		assertEquals(1, model.size());
		assertTrue(model.has("a"));
		ObjectNode linkModel = (ObjectNode) model.get("a");
		assertEquals(2, linkModel.size());
		assertEquals("https://example.com/{bootVersion}/a",
				linkModel.get("href").textValue());
		assertEquals(true, linkModel.get("templated").booleanValue());
	}

	@Test
	public void mergeSeveralLinksInArray() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com", "some description"));
		links.add(Link.create("a", "https://example.com/2"));
		ObjectNode model = LinkMapper.mapLinks(links);
		assertEquals(1, model.size());
		assertTrue(model.has("a"));
		ArrayNode linksModel = (ArrayNode) model.get("a");
		assertEquals(2, linksModel.size());
		assertEquals("https://example.com", linksModel.get(0).get("href").textValue());
		assertEquals("https://example.com/2", linksModel.get(1).get("href").textValue());
	}

	@Test
	public void keepOrdering() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("first", "https://example.com"));
		links.add(Link.create("second", "https://example.com"));
		ObjectNode model = LinkMapper.mapLinks(links);
		String json = model.toString();
		assertTrue(json.indexOf("first") < json.indexOf("second"));
	}

	@Test
	public void keepOrderingWithMultipleUrlForSameRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("first", "https://example.com"));
		links.add(Link.create("second", "https://example.com"));
		links.add(Link.create("first", "https://example.com"));
		ObjectNode model = LinkMapper.mapLinks(links);
		String json = model.toString();
		assertTrue(json.indexOf("first") < json.indexOf("second"));
	}

}
