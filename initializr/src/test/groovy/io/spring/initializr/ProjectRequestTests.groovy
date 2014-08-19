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

import static org.junit.Assert.assertEquals

/**
 * @author Stephane Nicoll
 */
class ProjectRequestTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none()

	@Test
	void resolve() {
		ProjectRequest request = new ProjectRequest()
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'web', 'security', 'spring-data').get()

		request.style << 'web' << 'spring-data'
		request.resolve(metadata)
		assertBootStarter(request.dependencies.get(0), 'web')
		assertBootStarter(request.dependencies.get(1), 'spring-data')
	}

	@Test
	void resolveFullMetadata() {
		ProjectRequest request = new ProjectRequest()
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', createDependency('org.foo', 'acme', '1.2.0')).get()
		request.style << 'org.foo:acme'
		request.resolve(metadata)
		assertDependency(request.dependencies.get(0), 'org.foo', 'acme', '1.2.0')
	}

	@Test
	void resolveUnknownSimpleIdAsSpringBootStarter() {
		ProjectRequest request = new ProjectRequest()
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar').get()

		request.style << 'org.foo:bar' << 'foo-bar'
		request.resolve(metadata)
		assertDependency(request.dependencies.get(0), 'org.foo', 'bar', null)
		assertBootStarter(request.dependencies.get(1), 'foo-bar')
	}

	@Test
	void resolveUnknownDependency() {
		ProjectRequest request = new ProjectRequest()
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('code', 'org.foo:bar').get()

		request.style << 'org.foo:acme' // does not exist and

		thrown.expect(IllegalArgumentException)
		thrown.expectMessage('org.foo:acme')
		request.resolve(metadata)
	}

	private static void assertBootStarter(InitializrMetadata.Dependency actual, String name) {
		InitializrMetadata.Dependency expected = new InitializrMetadata.Dependency()
		expected.asSpringBootStarter(name)
		assertDependency(actual, expected.groupId, expected.artifactId, expected.version)
	}

	private static InitializrMetadata.Dependency createDependency(String groupId, String artifactId, String version) {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
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
