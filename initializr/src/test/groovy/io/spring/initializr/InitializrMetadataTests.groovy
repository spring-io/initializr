/*
 * Copyright 2012-2014 the original author or authors.
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

package io.spring.initializr

import io.spring.initializr.support.InitializrMetadataBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
class InitializrMetadataTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	private final InitializrMetadata metadata = new InitializrMetadata()

	@Test
	void setCoordinatesFromId() {
		InitializrMetadata.Dependency dependency = createDependency('org.foo:bar:1.2.3')
		metadata.validateDependency(dependency)
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertEquals '1.2.3', dependency.version
		assertEquals 'org.foo:bar:1.2.3', dependency.id
	}

	@Test
	void setCoordinatesFromIdNoVersion() {
		InitializrMetadata.Dependency dependency = createDependency('org.foo:bar')
		metadata.validateDependency(dependency)
		assertEquals 'org.foo', dependency.groupId
		assertEquals 'bar', dependency.artifactId
		assertNull dependency.version
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	void setIdFromCoordinates() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		dependency.version = '1.0'
		metadata.validateDependency(dependency)
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	void setIdFromCoordinatesNoVersion() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.groupId = 'org.foo'
		dependency.artifactId = 'bar'
		metadata.validateDependency(dependency)
		assertEquals 'org.foo:bar', dependency.id
	}

	@Test
	void setIdFromSimpleName() {
		InitializrMetadata.Dependency dependency = createDependency('web')

		metadata.validateDependency(dependency)
		assertEquals 'org.springframework.boot', dependency.groupId
		assertEquals 'spring-boot-starter-web', dependency.artifactId
		assertNull dependency.version
		assertEquals 'web', dependency.id
	}

	@Test
	void invalidDependency() {
		thrown.expect(InvalidInitializrMetadataException)
		metadata.validateDependency(new InitializrMetadata.Dependency())
	}

	@Test
	void invalidIdFormatTooManyColons() {
		InitializrMetadata.Dependency dependency = createDependency('org.foo:bar:1.0:test:external')

		thrown.expect(InvalidInitializrMetadataException)
		metadata.validateDependency(dependency)
	}

	@Test
	void generateIdWithNoGroupId() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.artifactId = 'bar'
		thrown.expect(IllegalArgumentException)
		dependency.generateId()
	}

	@Test
	void generateIdWithNoArtifactId() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.groupId = 'foo'
		thrown.expect(IllegalArgumentException)
		dependency.generateId()
	}

	@Test
	void indexedDependencies() {
		InitializrMetadata metadata = new InitializrMetadata()
		InitializrMetadata.DependencyGroup group = new InitializrMetadata.DependencyGroup()

		InitializrMetadata.Dependency dependency = createDependency('first')
		group.content.add(dependency)
		InitializrMetadata.Dependency dependency2 = createDependency('second')
		group.content.add(dependency2)

		metadata.dependencies.add(group)

		metadata.validate()

		assertSame dependency, metadata.getDependency('first')
		assertSame dependency2, metadata.getDependency('second')
		assertNull metadata.getDependency('anotherId')
	}

	@Test
	void addTwoDependenciesWithSameId() {
		InitializrMetadata metadata = new InitializrMetadata()
		InitializrMetadata.DependencyGroup group = new InitializrMetadata.DependencyGroup()

		InitializrMetadata.Dependency dependency = createDependency('conflict')
		group.content.add(dependency)
		InitializrMetadata.Dependency dependency2 = createDependency('conflict')
		group.content.add(dependency2)

		metadata.dependencies.add(group)

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage('conflict')
		metadata.validate()
	}

	@Test
	void createProjectRequest() {
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults().get()
		ProjectRequest request = doCreateProjectRequest(metadata)
		assertEquals metadata.defaults.groupId, request.groupId
	}

	@Test
	void getDefaultNoDefault() {
		List elements = []
		elements << createJavaVersion('one', false) << createJavaVersion('two', false)
		assertEquals 'three', InitializrMetadata.getDefault(elements, 'three')
	}

	@Test
	void getDefaultWithDefault() {
		List elements = []
		elements << createJavaVersion('one', false) << createJavaVersion('two', true)
		assertEquals 'two', InitializrMetadata.getDefault(elements, 'three')
	}

	private static ProjectRequest doCreateProjectRequest(InitializrMetadata metadata) {
		ProjectRequest request = new ProjectRequest()
		metadata.initializeProjectRequest(request)
		request
	}

	private static InitializrMetadata.Dependency createDependency(String id) {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id = id
		dependency
	}

	private static InitializrMetadata.JavaVersion createJavaVersion(String version, boolean selected) {
		InitializrMetadata.JavaVersion javaVersion = new InitializrMetadata.JavaVersion()
		javaVersion.id = version
		javaVersion.default = selected
		javaVersion
	}

}
