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

package io.spring.initializr.web.mapper

import io.spring.initializr.metadata.Link
import org.junit.Test

/**
 * Tests for {@link LinkMapper}.
 *
 * @author Stephane Nicoll
 */
class LinkMapperTests {

	@Test
	void mapSimpleRel() {
		def links = new ArrayList()
		links << new Link(rel: 'a', 'href': 'https://example.com',
				description: 'some description')
		def model = LinkMapper.mapLinks(links)
		assert model.size() == 1
		assert model.containsKey('a')
		def linkModel = model['a']
		assert linkModel.size() == 2
		assert linkModel['href'] == 'https://example.com'
		assert linkModel['title'] == 'some description'
	}

	@Test
	void mapTemplatedRel() {
		def links = new ArrayList()
		links << new Link(rel: 'a', 'href': 'https://example.com/{bootVersion}/a',
				templated: true)
		def model = LinkMapper.mapLinks(links)
		assert model.size() == 1
		assert model.containsKey('a')
		def linkModel = model['a']
		assert linkModel.size() == 2
		assert linkModel['href'] == 'https://example.com/{bootVersion}/a'
		assert linkModel['templated'] == true
	}

	@Test
	void mergeSeveralLinksInArray() {
		def links = new ArrayList()
		links << new Link(rel: 'a', 'href': 'https://example.com',
				description: 'some description')
		links << new Link(rel: 'a', 'href': 'https://example.com/2')
		def model = LinkMapper.mapLinks(links)
		assert model.size() == 1
		assert model.containsKey('a')
		def linksModel = model['a']
		assert linksModel.size() == 2
		assert linksModel[0]['href'] == 'https://example.com'
		assert linksModel[1]['href'] == 'https://example.com/2'
	}

	@Test
	void keepOrdering() {
		def links = new ArrayList()
		links << new Link(rel: 'a', 'href': 'https://example.com')
		links << new Link(rel: 'b', 'href': 'https://example.com')
		def model = LinkMapper.mapLinks(links)
		def iterator = model.keySet().iterator()
		assert ++iterator == 'a'
		assert ++iterator == 'b'
	}

	@Test
	void keepOrderingWithMultipleUrlForSameRel() {
		def links = new ArrayList()
		links << new Link(rel: 'a', 'href': 'https://example.com')
		links << new Link(rel: 'b', 'href': 'https://example.com')
		links << new Link(rel: 'a', 'href': 'https://example.com')
		def model = LinkMapper.mapLinks(links)
		def iterator = model.keySet().iterator()
		assert ++iterator == 'a'
		assert ++iterator == 'b'
	}

}
