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
class TypeCapabilityTests {

	@Test
	void defaultEmpty() {
		TypeCapability capability = new TypeCapability()
		assertNull capability.default
	}

	@Test
	void defaultNoDefault() {
		TypeCapability capability = new TypeCapability()
		capability.content << new Type(id: 'foo', default: false)  << new Type(id: 'bar', default: false)
		assertNull capability.default
	}

	@Test
	void defaultType() {
		TypeCapability capability = new TypeCapability()
		Type first = new Type(id: 'foo', default: false)
		Type second = new Type(id: 'bar', default: true)
		capability.content << first  << second
		assertEquals second, capability.default
	}

	@Test
	void mergeAddEntry() {
		TypeCapability capability = new TypeCapability()
		def foo = new Type(id: 'foo', default: false)
		capability.content << foo

		TypeCapability anotherCapability = new TypeCapability()
		def foo2 =new Type(id: 'foo', default: true)
		def bar =new Type(id: 'bar', default: true)
		anotherCapability.content << foo2 << bar

		capability.merge(anotherCapability)
		assertEquals 2, capability.content.size()
		assertEquals foo, capability.get('foo')
		assertEquals bar, capability.get('bar')
		assertEquals bar, capability.default
	}

}
