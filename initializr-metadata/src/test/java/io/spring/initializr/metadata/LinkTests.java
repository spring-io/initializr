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

package io.spring.initializr.metadata;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Tests for {@link Link}.
 *
 * @author Stephane Nicoll
 */
class LinkTests {

	@Test
	void resolveInvalidLinkNoRel() {
		Link link = new Link();
		link.setHref("https://example.com");
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(link::resolve);
	}

	@Test
	void resolveInvalidLinkNoHref() {
		Link link = Link.create("reference", null, "foo doc");
		assertThatExceptionOfType(InvalidInitializrMetadataException.class)
				.isThrownBy(link::resolve);
	}

	@Test
	void resolveLinkNoVariables() {
		Link link = Link.create("reference", "https://example.com/2");
		link.resolve();
		assertThat(link.isTemplated()).isFalse();
		assertThat(link.getTemplateVariables()).isEmpty();
	}

	@Test
	void resolveLinkWithVariables() {
		Link link = Link.create("reference", "https://example.com/{a}/2/{b}");
		link.resolve();
		assertThat(link.isTemplated()).isTrue();
		assertThat(link.getTemplateVariables()).containsExactlyInAnyOrder("a", "b");
	}

	@Test
	void expandLink() throws Exception {
		Link link = Link.create("reference", "https://example.com/{a}/2/{b}");
		link.resolve();
		Map<String, String> map = new LinkedHashMap<>();
		map.put("a", "test");
		map.put("b", "another");
		assertThat(link.expand(map))
				.isEqualTo(new URI("https://example.com/test/2/another"));
	}

	@Test
	void expandLinkWithSameAttributeAtTwoPlaces() throws Exception {
		Link link = Link.create("reference", "https://example.com/{a}/2/{a}");
		link.resolve();
		Map<String, String> map = new LinkedHashMap<>();
		map.put("a", "test");
		map.put("b", "another");
		assertThat(link.expand(map))
				.isEqualTo(new URI("https://example.com/test/2/test"));
	}

	@Test
	void expandLinkMissingVariable() {
		Link link = Link.create("reference", "https://example.com/{a}/2/{b}");
		link.resolve();
		assertThatIllegalArgumentException()
				.isThrownBy(() -> link.expand(Collections.singletonMap("a", "test")))
				.withMessageContaining("missing value for 'b'");
	}

}
