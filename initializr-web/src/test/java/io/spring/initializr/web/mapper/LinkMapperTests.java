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
import java.util.Map;

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
		Map<String, Object> model = LinkMapper.mapLinks(links);
		assertEquals(1, model.size());
		assertTrue(model.containsKey("a"));
		@SuppressWarnings("unchecked")
		Map<String, Object> linkModel = (Map<String, Object>) model.get("a");
		assertEquals(2, linkModel.size());
		assertEquals("https://example.com", linkModel.get("href"));
		assertEquals("some description", linkModel.get("title"));
	}

	@Test
	public void mapTemplatedRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com/{bootVersion}/a", true));
		Map<String, Object> model = LinkMapper.mapLinks(links);
		assertEquals(1, model.size());
		assertTrue(model.containsKey("a"));
		@SuppressWarnings("unchecked")
		Map<String, Object> linkModel = (Map<String, Object>) model.get("a");
		assertEquals(2, linkModel.size());
		assertEquals("https://example.com/{bootVersion}/a", linkModel.get("href"));
		assertEquals(true, linkModel.get("templated"));
	}

	@Test
	public void mergeSeveralLinksInArray() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com", "some description"));
		links.add(Link.create("a", "https://example.com/2"));
		Map<String, Object> model = LinkMapper.mapLinks(links);
		assertEquals(1, model.size());
		assertTrue(model.containsKey("a"));
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> linksModel = (List<Map<String, Object>>) model.get("a");
		assertEquals(2, linksModel.size());
		assertEquals("https://example.com", linksModel.get(0).get("href"));
		assertEquals("https://example.com/2", linksModel.get(1).get("href"));
	}

	@Test
	public void keepOrdering() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com"));
		links.add(Link.create("b", "https://example.com"));
		Map<String, Object> model = LinkMapper.mapLinks(links);
		assertEquals("[a, b]", model.keySet().toString());
	}

	@Test
	public void keepOrderingWithMultipleUrlForSameRel() {
		List<Link> links = new ArrayList<>();
		links.add(Link.create("a", "https://example.com"));
		links.add(Link.create("b", "https://example.com"));
		links.add(Link.create("a", "https://example.com"));
		Map<String, Object> model = LinkMapper.mapLinks(links);
		assertEquals("[a, b]", model.keySet().toString());
	}

}
