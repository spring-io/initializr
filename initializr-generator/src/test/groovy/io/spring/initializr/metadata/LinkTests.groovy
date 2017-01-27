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

package io.spring.initializr.metadata

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/**
 * Tests for {@link Link}.
 *
 * @author Stephane Nicoll
 */
class LinkTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void resolveInvalidLinkNoRel() {
		def link = new Link(href: 'https://example.com')
		thrown.expect(InvalidInitializrMetadataException)
		link.resolve()
	}

	@Test
	void resolveInvalidLinkNoHref() {
		def link = new Link(rel: 'reference', description: 'foo doc')

		thrown.expect(InvalidInitializrMetadataException)
		link.resolve()
	}

	@Test
	void resolveLinkNoVariables() {
		def link = new Link(rel: 'reference', href: 'https://example.com/2')
		link.resolve()
		assert !link.templated
		assert link.templateVariables.size() == 0
	}

	@Test
	void resolveLinkWithVariables() {
		def link = new Link(rel: 'reference', href: 'https://example.com/{a}/2/{b}')
		link.resolve()
		assert link.templated
		assert link.templateVariables.size() == 2
		assert link.templateVariables.contains('a')
		assert link.templateVariables.contains('b')
	}

	@Test
	void expandLink() {
		def link = new Link(rel: 'reference', href: 'https://example.com/{a}/2/{b}')
		link.resolve()
		assert link.expand(['a': 'test', 'b': 'another']) ==
				new URI('https://example.com/test/2/another')
	}

	@Test
	void expandLinkWithSameAttributeAtTwoPlaces() {
		def link = new Link(rel: 'reference', href: 'https://example.com/{a}/2/{a}')
		link.resolve()
		assert link.expand(['a': 'test', 'b': 'another']) ==
				new URI('https://example.com/test/2/test')
	}

	@Test
	void expandLinkMissingVariable() {
		def link = new Link(rel: 'reference', href: 'https://example.com/{a}/2/{b}')
		link.resolve()

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage("missing value for 'b'")
		link.expand(['a': 'test'])
	}

}
