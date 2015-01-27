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

package io.spring.initializr

import io.spring.initializr.test.InitializrMetadataBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

/**
 * @author Stephane Nicoll
 */
class ProjectRequestTests {

	private static final String DEFAULT_APPLICATION_NAME = 'FooBarApplication'

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void resolve() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'web', 'security', 'spring-data').validateAndGet()

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
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'web', 'security', 'spring-data').validateAndGet()

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
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', createDependency('org.foo', 'acme', '1.2.0')).validateAndGet()
		request.style << 'org.foo:acme'
		request.resolve(metadata)
		assertDependency(request.resolvedDependencies[0], 'org.foo', 'acme', '1.2.0')
	}

	@Test
	void resolveUnknownSimpleIdAsSpringBootStarter() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar').validateAndGet()

		request.style << 'org.foo:bar' << 'foo-bar'
		request.resolve(metadata)
		assertDependency(request.resolvedDependencies[0], 'org.foo', 'bar', null)
		assertBootStarter(request.resolvedDependencies[1], 'foo-bar')
	}

	@Test
	void resolveUnknownDependency() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar').validateAndGet()

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
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', dependency).validateAndGet()

		request.style << 'org.foo:bar'
		request.bootVersion = '1.1.2.RELEASE'
		request.resolve(metadata)
	}

	@Test
	void resolveDependencyNotInRange() {
		def request = new ProjectRequest()
		def dependency = createDependency('org.foo', 'bar', '1.2.0.RELEASE')
		dependency.versionRange = '[1.0.1.RELEASE, 1.2.0.RELEASE)'
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', dependency).validateAndGet()

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
		def metadata = InitializrMetadataBuilder.withDefaults().validateAndGet()
		request.type = 'gradle-project'

		request.resolve(metadata)
		assertEquals 'gradle', request.build
	}

	@Test
	void resolveBuildNoTag() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataBuilder.withDefaults()
				.addType('foo', false, '/foo.zip', null, null).validateAndGet()
		request.type = 'foo'

		request.resolve(metadata)
		assertNull request.build
	}

	@Test
	void resolveUnknownType() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataBuilder.withDefaults().validateAndGet()
		request.type = 'foo-project'

		thrown.expect(InvalidProjectRequestException)
		thrown.expectMessage('foo-project')
		request.resolve(metadata)
	}

	@Test
	void resolveApplicationNameWithNoName() {
		def request = new ProjectRequest()
		def metadata = InitializrMetadataBuilder.withDefaults().validateAndGet()

		request.resolve(metadata)
		assertEquals ProjectRequest.DEFAULT_APPLICATION_NAME, request.applicationName
	}

	@Test
	void resolveApplicationName() {
		def request = new ProjectRequest()
		request.name = 'Foo2'
		def metadata = InitializrMetadataBuilder.withDefaults().validateAndGet()

		request.resolve(metadata)
		assertEquals 'Foo2Application', request.applicationName
	}

	@Test
	void resolveApplicationNameWithApplicationNameSet() {
		def request = new ProjectRequest()
		request.name = 'Foo2'
		request.applicationName ='MyApplicationName'
		def metadata = InitializrMetadataBuilder.withDefaults().validateAndGet()

		request.resolve(metadata)
		assertEquals 'MyApplicationName', request.applicationName
	}

	@Test
	void generateApplicationNameSimple() {
		assertEquals 'DemoApplication', generateApplicationName('demo')
	}

	@Test
	void generateApplicationNameSimpleApplication() {
		assertEquals 'DemoApplication', generateApplicationName('demoApplication')
	}

	@Test
	void generateApplicationNameSimpleCamelCase() {
		assertEquals 'MyDemoApplication', generateApplicationName('myDemo')
	}

	@Test
	void generateApplicationNameSimpleUnderscore() {
		assertEquals 'MyDemoApplication', generateApplicationName('my_demo')
	}

	@Test
	void generateApplicationNameSimpleColon() {
		assertEquals 'MyDemoApplication', generateApplicationName('my:demo')
	}

	@Test
	void generateApplicationNameSimpleSpace() {
		assertEquals 'MyDemoApplication', generateApplicationName('my demo')
	}

	@Test
	void generateApplicationNamSsimpleDash() {
		assertEquals 'MyDemoApplication', generateApplicationName('my-demo')
	}

	@Test
	void generateApplicationNameUpperCaseUnderscore() {
		assertEquals 'MyDemoApplication', generateApplicationName('MY_DEMO')
	}

	@Test
	void generateApplicationNameUpperCaseDash() {
		assertEquals 'MyDemoApplication', generateApplicationName('MY-DEMO')
	}

	@Test
	void generateApplicationNameMultiSpaces() {
		assertEquals 'MyDemoApplication', generateApplicationName('   my    demo ')
	}

	@Test
	void generateApplicationNameMultiSpacesUpperCase() {
		assertEquals 'MyDemoApplication', generateApplicationName('   MY    DEMO ')
	}

	@Test
	void generateApplicationNameInvalidStartCharacter() {
		assertEquals DEFAULT_APPLICATION_NAME, generateApplicationName('1MyDemo')
	}

	@Test
	void generateApplicationNameInvalidPartCharacter() {
		assertEquals DEFAULT_APPLICATION_NAME, generateApplicationName('MyDe|mo')
	}

	private static generateApplicationName(String text) {
		ProjectRequest.generateApplicationName(text, DEFAULT_APPLICATION_NAME)
	}

	private static void assertBootStarter(InitializrMetadata.Dependency actual, String name) {
		def expected = new InitializrMetadata.Dependency()
		expected.asSpringBootStarter(name)
		assertDependency(actual, expected.groupId, expected.artifactId, expected.version)
		assertEquals name, actual.id
	}

	private static InitializrMetadata.Dependency createDependency(String groupId, String artifactId, String version) {
		def dependency = new InitializrMetadata.Dependency()
		dependency.groupId = groupId
		dependency.artifactId = artifactId
		dependency.version = version
		dependency
	}

	private static void assertDependency(InitializrMetadata.Dependency actual, String groupId,
										 String artifactId, String version) {
		assertEquals groupId, actual.groupId
		assertEquals artifactId, actual.artifactId
		assertEquals version, actual.version
	}
}
