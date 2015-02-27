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

import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.assertNull
import static org.junit.Assert.assertSame

/**
 * @author Stephane Nicoll
 */
class DependenciesCapabilityTest {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void indexedDependencies() {
		def dependency = new Dependency(id: 'first')
		def dependency2 = new Dependency(id: 'second')
		def capability = createDependenciesCapability('foo', dependency, dependency2)
		capability.validate()

		assertSame dependency, capability.get('first')
		assertSame dependency2, capability.get('second')
		assertNull capability.get('anotherId')
	}

	@Test
	void addTwoDependenciesWithSameId() {
		def dependency = new Dependency(id: 'conflict')
		def dependency2 = new Dependency(id: 'conflict')
		def capability = createDependenciesCapability('foo', dependency, dependency2)

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage('conflict')
		capability.validate()
	}

	@Test
	void addDependencyWithAliases() {
		def dependency = new Dependency(id: 'first')
		dependency.aliases.add('alias1')
		dependency.aliases.add('alias2')
		def capability = createDependenciesCapability('foo', dependency)
		capability.validate()

		assertSame dependency, capability.get('first')
		assertSame dependency, capability.get('alias1')
		assertSame dependency, capability.get('alias2')
	}

	@Test
	void aliasClashWithAnotherDependency() {
		def dependency = new Dependency(id: 'first')
		dependency.aliases.add('alias1')
		dependency.aliases.add('alias2')
		def dependency2 = new Dependency(id: 'alias2')

		def capability = new DependenciesCapability()
		capability.content << createDependencyGroup('foo', dependency)
		capability.content << createDependencyGroup('bar', dependency2)

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage('alias2')
		capability.validate()
	}

	private static DependenciesCapability createDependenciesCapability(String groupName, Dependency... dependencies) {
		DependenciesCapability capability = new DependenciesCapability()
		DependencyGroup group = createDependencyGroup(groupName, dependencies)
		capability.content << group
		capability
	}

	private static DependencyGroup createDependencyGroup(String groupName, Dependency... dependencies) {
		DependencyGroup group = new DependencyGroup(name: groupName)
		for (Dependency dependency : dependencies) {
			group.content << dependency
		}
		group
	}

}
