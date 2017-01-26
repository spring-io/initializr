/*
 * Copyright 2012-2016 the original author or authors.
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

package io.spring.initializr.actuate.stat

import io.spring.initializr.generator.ProjectFailedEvent
import io.spring.initializr.generator.ProjectGeneratedEvent
import io.spring.initializr.generator.ProjectRequest
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue

/**
 *
 * @author Stephane Nicoll
 */
class ProjectRequestDocumentFactoryTests extends AbstractInitializrStatTests {

	private final ProjectRequestDocumentFactory factory =
			new ProjectRequestDocumentFactory(createProvider(metadata))

	@Test
	void createDocumentForSimpleProject() {
		ProjectRequest request = createProjectRequest()
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals event.timestamp, document.generationTimestamp
		assertEquals null, document.requestIp
		assertEquals 'com.example', document.groupId
		assertEquals 'demo', document.artifactId
		assertEquals 'com.example', document.packageName
		assertEquals '1.2.3.RELEASE', document.bootVersion
		assertEquals '1.8', document.javaVersion
		assertEquals 'java', document.language
		assertEquals 'jar', document.packaging
		assertEquals 'maven-project', document.type
		assertEquals 0, document.dependencies.size()
		assertValid document
	}

	@Test
	void createDocumentWithRequestIp() {
		ProjectRequest request = createProjectRequest()
		request.parameters['x-forwarded-for'] = '10.0.0.123'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals '10.0.0.123', document.requestIp
		assertEquals '10.0.0.123', document.requestIpv4
		assertNull document.requestCountry
	}

	@Test
	void createDocumentWithRequestIpv6() {
		ProjectRequest request = createProjectRequest()
		request.parameters['x-forwarded-for'] = '2001:db8:a0b:12f0::1'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals '2001:db8:a0b:12f0::1', document.requestIp
		assertNull document.requestIpv4
		assertNull document.requestCountry
	}

	@Test
	void createDocumentWithCloudFlareHeaders() {
		ProjectRequest request = createProjectRequest()
		request.parameters['cf-connecting-ip'] = '10.0.0.123'
		request.parameters['cf-ipcountry'] = 'BE'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals '10.0.0.123', document.requestIp
		assertEquals '10.0.0.123', document.requestIpv4
		assertEquals 'BE', document.requestCountry
	}

	@Test
	void createDocumentWithCloudFlareIpv6() {
		ProjectRequest request = createProjectRequest()
		request.parameters['cf-connecting-ip'] = '2001:db8:a0b:12f0::1'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals '2001:db8:a0b:12f0::1', document.requestIp
		assertNull document.requestIpv4
		assertNull document.requestCountry
	}

	@Test
	void createDocumentWithCloudFlareHeadersAndOtherHeaders() {
		ProjectRequest request = createProjectRequest()
		request.parameters['cf-connecting-ip'] = '10.0.0.123'
		request.parameters['x-forwarded-for'] = '192.168.1.101'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals '10.0.0.123', document.requestIp
		assertEquals '10.0.0.123', document.requestIpv4
		assertNull document.requestCountry
	}

	@Test
	void createDocumentWithCloudFlareCountrySetToXX() {
		ProjectRequest request = createProjectRequest()
		request.parameters['cf-connecting-ip'] = 'Xx' // case insensitive
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertNull document.requestCountry
	}

	@Test
	void createDocumentWithUserAgent() {
		ProjectRequest request = createProjectRequest()
		request.parameters['user-agent'] = 'HTTPie/0.8.0'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals 'httpie', document.clientId
		assertEquals '0.8.0', document.clientVersion
	}

	@Test
	void createDocumentWithUserAgentNoVersion() {
		ProjectRequest request = createProjectRequest()
		request.parameters['user-agent'] = 'IntelliJ IDEA'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals 'intellijidea', document.clientId
		assertEquals null, document.clientVersion
	}

	@Test
	void createDocumentInvalidJavaVersion() {
		ProjectRequest request = createProjectRequest()
		request.javaVersion = '1.2'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals '1.2', document.javaVersion
		assertTrue document.invalid
		assertTrue document.invalidJavaVersion
	}

	@Test
	void createDocumentInvalidLanguage() {
		ProjectRequest request = createProjectRequest()
		request.language = 'c++'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals 'c++', document.language
		assertTrue document.invalid
		assertTrue document.invalidLanguage
	}

	@Test
	void createDocumentInvalidPackaging() {
		ProjectRequest request = createProjectRequest()
		request.packaging = 'ear'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals 'ear', document.packaging
		assertTrue document.invalid
		assertTrue document.invalidPackaging
	}

	@Test
	void createDocumentInvalidType() {
		ProjectRequest request = createProjectRequest()
		request.type = 'ant-project'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals 'ant-project', document.type
		assertTrue document.invalid
		assertTrue document.invalidType
	}

	@Test
	void createDocumentInvalidDependency() {
		ProjectRequest request = createProjectRequest()
		request.dependencies << 'web' << 'invalid' << 'data-jpa' << 'invalid-2'
		def event = new ProjectGeneratedEvent(request)
		def document = factory.createDocument(event)
		assertEquals 'web', document.dependencies[0]
		assertEquals 'data-jpa', document.dependencies[1]
		assertEquals 2, document.dependencies.size()
		assertTrue document.invalid
		assertEquals 'invalid', document.invalidDependencies[0]
		assertEquals 'invalid-2', document.invalidDependencies[1]
		assertEquals 2, document.invalidDependencies.size()
	}

	@Test
	void createDocumentWithProjectFailedEvent() {
		ProjectRequest request = createProjectRequest()
		def event = new ProjectFailedEvent(request, new IllegalStateException('my test message'))
		def document = factory.createDocument(event)
		assertTrue document.invalid
		assertEquals 'my test message', document.errorMessage
	}

	private static void assertValid(ProjectRequestDocument document) {
		assertFalse document.invalid
		assertFalse document.invalidJavaVersion
		assertFalse document.invalidLanguage
		assertFalse document.invalidPackaging
		assertEquals 0, document.invalidDependencies.size()
	}

}
