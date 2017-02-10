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

package io.spring.initializr.metadata;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link Link}.
 *
 * @author Stephane Nicoll
 */
public class LinkTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void resolveInvalidLinkNoRel() {
		Link link = new Link();
		link.setHref("https://example.com");
		thrown.expect(InvalidInitializrMetadataException.class);
		link.resolve();
	}

	@Test
	public void resolveInvalidLinkNoHref() {
		Link link = Link.create("reference", null, "foo doc");
		thrown.expect(InvalidInitializrMetadataException.class);
		link.resolve();
	}

	@Test
	public void resolveLinkNoVariables() {
		Link link = Link.create("reference", "https://example.com/2");
		link.resolve();
		assertFalse(link.isTemplated());
		assertEquals(0, link.getTemplateVariables().size());
	}

	@Test
	public void resolveLinkWithVariables() {
		Link link = Link.create("reference", "https://example.com/{a}/2/{b}");
		link.resolve();
		assertTrue(link.isTemplated());
		assertEquals(2, link.getTemplateVariables().size());
		assertTrue(link.getTemplateVariables().contains("a"));
		assertTrue(link.getTemplateVariables().contains("b"));
	}

	@Test
	public void expandLink() throws Exception {
		Link link = Link.create("reference", "https://example.com/{a}/2/{b}");
		link.resolve();
		Map<String, String> map = new LinkedHashMap<>();
		map.put("a", "test");
		map.put("b", "another");
		assertEquals(new URI("https://example.com/test/2/another"), link.expand(map));
	}

	@Test
	public void expandLinkWithSameAttributeAtTwoPlaces() throws Exception {
		Link link = Link.create("reference", "https://example.com/{a}/2/{a}");
		link.resolve();
		Map<String, String> map = new LinkedHashMap<>();
		map.put("a", "test");
		map.put("b", "another");
		assertEquals(new URI("https://example.com/test/2/test"), link.expand(map));
	}

	@Test
	public void expandLinkMissingVariable() {
		Link link = Link.create("reference", "https://example.com/{a}/2/{b}");
		link.resolve();

		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("missing value for 'b'");
		link.expand(Collections.singletonMap("a", "test"));
	}

}
