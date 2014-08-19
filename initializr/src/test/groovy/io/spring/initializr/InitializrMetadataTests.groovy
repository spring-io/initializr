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
		InitializrMetadata.Dependency dependency = createDependency('first')
		InitializrMetadata.Dependency dependency2 = createDependency('second')


		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('foo', dependency, dependency2).validateAndGet()

		assertSame dependency, metadata.getDependency('first')
		assertSame dependency2, metadata.getDependency('second')
		assertNull metadata.getDependency('anotherId')
	}

	@Test
	void addTwoDependenciesWithSameId() {
		InitializrMetadata.Dependency dependency = createDependency('conflict')
		InitializrMetadata.Dependency dependency2 = createDependency('conflict')

		InitializrMetadataBuilder builder = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('foo', dependency, dependency2)

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage('conflict')
		builder.validateAndGet()
	}

	@Test
	void addDependencyWithAliases() {
		InitializrMetadata.Dependency dependency = createDependency('first')
		dependency.aliases.add('alias1')
		dependency.aliases.add('alias2')

		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('foo', dependency).validateAndGet()

		assertSame dependency, metadata.getDependency('first')
		assertSame dependency, metadata.getDependency('alias1')
		assertSame dependency, metadata.getDependency('alias2')
	}

	@Test
	void aliasClashWithAnotherDependency() {
		InitializrMetadata.Dependency dependency = createDependency('first')
		dependency.aliases.add('alias1')
		dependency.aliases.add('alias2')

		InitializrMetadata.Dependency dependency2 = createDependency('alias2')

		InitializrMetadataBuilder builder = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('foo', dependency)
				.addDependencyGroup('bar', dependency2)

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage('alias2')
		builder.validateAndGet()
	}

	@Test
	void createProjectRequest() {
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults().validateAndGet()
		ProjectRequest request = doCreateProjectRequest(metadata)
		assertEquals metadata.defaults.groupId, request.groupId
	}

	@Test
	void validateArtifactRepository() {
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults().instance()
		metadata.env.artifactRepository = 'http://foo/bar'
		metadata.validate()
		assertEquals 'http://foo/bar/', metadata.env.artifactRepository
	}

	@Test
	void getDefaultNoDefault() {
		List elements = []
		elements << createJavaVersion('one', false) << createJavaVersion('two', false)
		assertEquals 'one', InitializrMetadata.getDefault(elements)
	}

	@Test
	void getDefaultEmpty() {
		List elements = []
		assertNull InitializrMetadata.getDefault(elements)
	}

	@Test
	void getDefault() {
		List elements = []
		elements << createJavaVersion('one', false) << createJavaVersion('two', true)
		assertEquals 'two', InitializrMetadata.getDefault(elements)
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
