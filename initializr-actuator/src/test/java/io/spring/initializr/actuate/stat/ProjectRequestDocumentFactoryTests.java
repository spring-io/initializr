/*
 * Copyright 2012-2017 the original author or authors.
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

package io.spring.initializr.actuate.stat;

import java.util.Arrays;

import io.spring.initializr.generator.ProjectFailedEvent;
import io.spring.initializr.generator.ProjectGeneratedEvent;
import io.spring.initializr.generator.ProjectRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Stephane Nicoll
 */
public class ProjectRequestDocumentFactoryTests extends AbstractInitializrStatTests {

	private final ProjectRequestDocumentFactory factory =
			new ProjectRequestDocumentFactory(createProvider(getMetadata()));

	@Test
	public void createDocumentForSimpleProject() {
		ProjectRequest request = createProjectRequest();
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals(event.getTimestamp(), document.getGenerationTimestamp());
		assertEquals(null, document.getRequestIp());
		assertEquals("com.example", document.getGroupId());
		assertEquals("demo", document.getArtifactId());
		assertEquals("com.example.demo", document.getPackageName());
		assertEquals("1.2.3.RELEASE", document.getBootVersion());
		assertEquals("1.8", document.getJavaVersion());
		assertEquals("java", document.getLanguage());
		assertEquals("jar", document.getPackaging());
		assertEquals("maven-project", document.getType());
		assertEquals(0, document.getDependencies().size());
		assertValid(document);
	}

	@Test
	public void createDocumentWithRequestIp() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "10.0.0.123");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("10.0.0.123", document.getRequestIp());
		assertEquals("10.0.0.123", document.getRequestIpv4());
		assertNull(document.getRequestCountry());
	}

	@Test
	public void createDocumentWithRequestIpv6() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("2001:db8:a0b:12f0::1", document.getRequestIp());
		assertNull(document.getRequestIpv4());
		assertNull(document.getRequestCountry());
	}

	@Test
	public void createDocumentWithCloudFlareHeaders() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("cf-ipcountry", "BE");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("10.0.0.123", document.getRequestIp());
		assertEquals("10.0.0.123", document.getRequestIpv4());
		assertEquals("BE", document.getRequestCountry());
	}

	@Test
	public void createDocumentWithCloudFlareIpv6() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("2001:db8:a0b:12f0::1", document.getRequestIp());
		assertNull(document.getRequestIpv4());
		assertNull(document.getRequestCountry());
	}

	@Test
	public void createDocumentWithCloudFlareHeadersAndOtherHeaders() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("x-forwarded-for", "192.168.1.101");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("10.0.0.123", document.getRequestIp());
		assertEquals("10.0.0.123", document.getRequestIpv4());
		assertNull(document.getRequestCountry());
	}

	@Test
	public void createDocumentWithCloudFlareCountrySetToXX() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "Xx"); // case insensitive
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertNull(document.getRequestCountry());
	}

	@Test
	public void createDocumentWithUserAgent() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "HTTPie/0.8.0");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("httpie", document.getClientId());
		assertEquals("0.8.0", document.getClientVersion());
	}

	@Test
	public void createDocumentWithUserAgentNoVersion() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "IntelliJ IDEA");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("intellijidea", document.getClientId());
		assertEquals(null, document.getClientVersion());
	}

	@Test
	public void createDocumentInvalidJavaVersion() {
		ProjectRequest request = createProjectRequest();
		request.setJavaVersion("1.2");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("1.2", document.getJavaVersion());
		assertTrue(document.isInvalid());
		assertTrue(document.isInvalidJavaVersion());
	}

	@Test
	public void createDocumentInvalidLanguage() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("c++");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("c++", document.getLanguage());
		assertTrue(document.isInvalid());
		assertTrue(document.isInvalidLanguage());
	}

	@Test
	public void createDocumentInvalidPackaging() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("ear");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("ear", document.getPackaging());
		assertTrue(document.isInvalid());
		assertTrue(document.isInvalidPackaging());
	}

	@Test
	public void createDocumentInvalidType() {
		ProjectRequest request = createProjectRequest();
		request.setType("ant-project");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("ant-project", document.getType());
		assertTrue(document.isInvalid());
		assertTrue(document.isInvalidType());
	}

	@Test
	public void createDocumentInvalidDependency() {
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Arrays.asList("web", "invalid", "data-jpa", "invalid-2"));
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = factory.createDocument(event);
		assertEquals("web", document.getDependencies().get(0));
		assertEquals("data-jpa", document.getDependencies().get(1));
		assertEquals(2, document.getDependencies().size());
		assertTrue(document.isInvalid());
		assertEquals("invalid", document.getInvalidDependencies().get(0));
		assertEquals("invalid-2", document.getInvalidDependencies().get(1));
		assertEquals(2, document.getInvalidDependencies().size());
	}

	@Test
	public void createDocumentWithProjectFailedEvent() {
		ProjectRequest request = createProjectRequest();
		ProjectFailedEvent event = new ProjectFailedEvent(request,
				new IllegalStateException("my test message"));
		ProjectRequestDocument document = factory.createDocument(event);
		assertTrue(document.isInvalid());
		assertEquals("my test message", document.getErrorMessage());
	}

	private static void assertValid(ProjectRequestDocument document) {
		assertFalse(document.isInvalid());
		assertFalse(document.isInvalidJavaVersion());
		assertFalse(document.isInvalidLanguage());
		assertFalse(document.isInvalidPackaging());
		assertEquals(0, document.getInvalidDependencies().size());
	}

}
