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

package io.spring.initializr.generator

import io.spring.initializr.InitializrConfiguration
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.test.InitializrMetadataTestBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * @author Stephane Nicoll
 */
class ProjectRequestTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void initializeProjectRequest() {
		def metadata = new InitializrMetadata(new InitializrConfiguration())
		metadata.groupId.content = 'org.acme'
		metadata.artifactId.content = 'my-project'
		ProjectRequest request = new ProjectRequest()
		request.initialize(metadata)
		assertEquals 'org.acme', request.groupId
		assertEquals 'my-project', request.artifactId
	}

	@Test
	void initializeProjectRequestWithDefaults() {
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()
		ProjectRequest request = new ProjectRequest()
		request.initialize(metadata)
		assertEquals metadata.name.content, request.name
		assertEquals metadata.types.default.id, request.type
		assertEquals metadata.description.content, request.description
		assertEquals metadata.groupId.content, request.groupId
		assertEquals metadata.artifactId.content, request.artifactId
		assertEquals metadata.version.content, request.version
		assertEquals metadata.bootVersions.default.id, request.bootVersion
		assertEquals metadata.packagings.default.id, request.packaging
	}

	@Test
	void resolve() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', 'web', 'security', 'spring-data').build()

		request.type = 'maven-project'
		request.style << 'web' << 'spring-data'
		request.resolve(metadata)
		assertEquals 'Build type not detected', 'maven', request.build
		assertBootStarter(request.resolvedDependencies[0], 'web')
		assertBootStarter(request.resolvedDependencies[1], 'spring-data')
	}

	@Test
	void resolveWithDependencies() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', 'web', 'security', 'spring-data').build()

		request.type = 'maven-project'
		request.dependencies << 'web' << 'spring-data'
		request.resolve(metadata)
		assertEquals 'Build type not detected', 'maven', request.build
		assertBootStarter(request.resolvedDependencies[0], 'web')
		assertBootStarter(request.resolvedDependencies[1], 'spring-data')
	}

	@Test
	void resolveFullMetadata() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', createDependency('org.foo', 'acme', '1.2.0')).build()
		request.style << 'org.foo:acme'
		request.resolve(metadata)
		assertDependency(request.resolvedDependencies[0], 'org.foo', 'acme', '1.2.0')
	}

	@Test
	void resolveUnknownSimpleIdAsSpringBootStarter() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar').build()

		request.style << 'org.foo:bar' << 'foo-bar'
		request.resolve(metadata)
		assertDependency(request.resolvedDependencies[0], 'org.foo', 'bar', null)
		assertBootStarter(request.resolvedDependencies[1], 'foo-bar')
	}

	@Test
	void resolveUnknownDependency() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar').build()

		request.style << 'org.foo:acme' // does not exist and

		thrown.expect(InvalidProjectRequestException)
		thrown.expectMessage('org.foo:acme')
		request.resolve(metadata)
		assertEquals(1, request.resolvedDependencies.size())
	}

	@Test
	void resolveDependencyInRange() {
		def request = new ProjectRequest()
		def dependency = createDependency('org.foo', 'bar', '1.2.0.RELEASE')
		dependency.versionRange = '[1.0.1.RELEASE, 1.2.0.RELEASE)'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', dependency).build()

		request.style << 'org.foo:bar'
		request.bootVersion = '1.1.2.RELEASE'
		request.resolve(metadata)
	}

	@Test
	void resolveDependencyNotInRange() {
		def request = new ProjectRequest()
		def dependency = createDependency('org.foo', 'bar', '1.2.0.RELEASE')
		dependency.versionRange = '[1.0.1.RELEASE, 1.2.0.RELEASE)'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('code', dependency).build()

		request.style << 'org.foo:bar'
		request.bootVersion = '0.9.9.RELEASE'

		thrown.expect(InvalidProjectRequestException)
		thrown.expectMessage('org.foo:bar')
		thrown.expectMessage('0.9.9.RELEASE')
		request.resolve(metadata)
	}

	@Test
	void resolveBuild() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()
		request.type = 'gradle-project'

		request.resolve(metadata)
		assertEquals 'gradle', request.build
	}

	@Test
	void resolveBuildNoTag() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addType('foo', false, '/foo.zip', null, null).build()
		request.type = 'foo'

		request.resolve(metadata)
		assertNull request.build
	}

	@Test
	void resolveUnknownType() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()
		request.type = 'foo-project'

		thrown.expect(InvalidProjectRequestException)
		thrown.expectMessage('foo-project')
		request.resolve(metadata)
	}

	@Test
	void resolveApplicationNameWithNoName() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()

		request.resolve(metadata)
		assertEquals metadata.configuration.env.fallbackApplicationName, request.applicationName
	}

	@Test
	void resolveApplicationName() {
		def request = new ProjectRequest()
		request.name = 'Foo2'
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()

		request.resolve(metadata)
		assertEquals 'Foo2Application', request.applicationName
	}

	@Test
	void resolveApplicationNameWithApplicationNameSet() {
		def request = new ProjectRequest()
		request.name = 'Foo2'
		request.applicationName ='MyApplicationName'
		def metadata = InitializrMetadataTestBuilder.withDefaults().build()

		request.resolve(metadata)
		assertEquals 'MyApplicationName', request.applicationName
	}


	private static void assertBootStarter(Dependency actual, String name) {
		def expected = new Dependency()
		expected.asSpringBootStarter(name)
		assertDependency(actual, expected.groupId, expected.artifactId, expected.version)
		assertEquals name, actual.id
	}

	private static Dependency createDependency(String groupId, String artifactId, String version) {
		new Dependency(groupId: groupId, artifactId: artifactId, version: version)
	}

	private static void assertDependency(Dependency actual, String groupId,
										 String artifactId, String version) {
		assertEquals groupId, actual.groupId
		assertEquals artifactId, actual.artifactId
		assertEquals version, actual.version
	}
}
