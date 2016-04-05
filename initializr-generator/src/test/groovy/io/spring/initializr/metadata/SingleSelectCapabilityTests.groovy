/*
 * Copyright 2012-2015 the original author or authors.
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

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * @author Stephane Nicoll
 */
class SingleSelectCapabilityTests {

	@Test
	void defaultEmpty() {
		SingleSelectCapability capability = new SingleSelectCapability('test')
		assertNull capability.default
	}

	@Test
	void defaultNoDefault() {
		SingleSelectCapability capability = new SingleSelectCapability('test')
		capability.content << new DefaultMetadataElement(id: 'foo', default: false)
		capability.content << new DefaultMetadataElement(id: 'bar', default: false)
		assertNull capability.default
	}

	@Test
	void defaultType() {
		SingleSelectCapability capability = new SingleSelectCapability('test')
		DefaultMetadataElement first = new DefaultMetadataElement(id: 'foo', default: false)
		DefaultMetadataElement second = new DefaultMetadataElement(id: 'bar', default: true)
		capability.content << first  << second
		assertEquals second, capability.default
	}

	@Test
	void mergeAddEntry() {
		SingleSelectCapability capability = new SingleSelectCapability('test')
		def foo = new DefaultMetadataElement(id: 'foo', default: false)
		capability.content << foo

		SingleSelectCapability anotherCapability = new SingleSelectCapability('test')
		def bar = new DefaultMetadataElement(id: 'bar', default: false)
		anotherCapability.content << bar

		capability.merge(anotherCapability)
		assertEquals 2, capability.content.size()
		assertEquals foo, capability.get('foo')
		assertEquals bar, capability.get('bar')
	}

}
