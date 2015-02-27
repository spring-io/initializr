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

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * @author Stephane Nicoll
 */
class DependencyTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void createRootSpringBootStarter() {
		Dependency d = new Dependency();
		d.asSpringBootStarter("")
		assertEquals 'org.springframework.boot', d.groupId
		assertEquals 'spring-boot-starter', d.artifactId
	}

	@Test
	void setCoordinatesFromId() {
		def dependency = new Dependency(id: 'org.foo:bar:1.2.3')
		dependency.resolve()
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertEquals '1.2.3', dependency.version
		assertEquals 'org.foo:bar:1.2.3', dependency.id
	}

	@Test
	void setCoordinatesFromIdNoVersion() {
		def dependency = new Dependency(id: 'org.foo:bar')
		dependency.resolve()
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertNull dependency.version
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	void setIdFromCoordinates() {
		def dependency = new Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		dependency.version = '1.0'
		dependency.resolve()
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	void setIdFromCoordinatesNoVersion() {
		def dependency = new Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		dependency.resolve()
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	void setIdFromSimpleName() {
		def dependency = new Dependency(id: 'web')
		dependency.resolve()
		assertEquals 'org.springframework.boot', dependency.groupId
		assertEquals 'spring-boot-starter-web', dependency.artifactId
		assertNull dependency.version
		assertEquals 'web', dependency.id
	}

	@Test
	void invalidDependency() {
		thrown.expect(InvalidInitializrMetadataException)
		new Dependency().resolve()
	}

	@Test
	void invalidDependencyScope() {
		def dependency = new Dependency(id: 'web')

		thrown.expect(InvalidInitializrMetadataException)
		dependency.setScope('whatever')
	}

	@Test
	void invalidSpringBootRange() {
		def dependency = new Dependency(id: 'web')
		dependency.versionRange = 'A.B.C'

		thrown.expect(InvalidInitializrMetadataException)
		thrown.expectMessage('A.B.C')
		dependency.resolve()
	}

	@Test
	void invalidIdFormatTooManyColons() {
		def dependency = new Dependency(id: 'org.foo:bar:1.0:test:external')

		thrown.expect(InvalidInitializrMetadataException)
		dependency.resolve()
	}

	@Test
	void generateIdWithNoGroupId() {
		def dependency = new Dependency()
		dependency.artifactId = 'bar'
		thrown.expect(IllegalArgumentException)
		dependency.generateId()
	}

	@Test
	void generateIdWithNoArtifactId() {
		def dependency = new Dependency()
		dependency.groupId = 'foo'
		thrown.expect(IllegalArgumentException)
		dependency.generateId()
	}

}
