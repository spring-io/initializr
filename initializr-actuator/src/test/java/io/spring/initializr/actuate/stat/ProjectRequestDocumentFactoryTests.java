/*
 * Copyright 2012-2018 the original author or authors.
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
public class ProjectRequestDocumentFactoryTests extends AbstractInitializrStatTests {

	private final ProjectRequestDocumentFactory factory = new ProjectRequestDocumentFactory(
			createProvider(getMetadata()));

	@Test
	public void createDocumentForSimpleProject() {
		ProjectRequest request = createProjectRequest();
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getGenerationTimestamp()).isEqualTo(event.getTimestamp());
		assertThat(document.getRequestIp()).isEqualTo(null);
		assertThat(document.getGroupId()).isEqualTo("com.example");
		assertThat(document.getArtifactId()).isEqualTo("demo");
		assertThat(document.getPackageName()).isEqualTo("com.example.demo");
		assertThat(document.getBootVersion()).isEqualTo("1.2.3.RELEASE");
		assertThat(document.getJavaVersion()).isEqualTo("1.8");
		assertThat(document.getLanguage()).isEqualTo("java");
		assertThat(document.getPackaging()).isEqualTo("jar");
		assertThat(document.getType()).isEqualTo("maven-project");
		assertThat(document.getDependencies()).isEmpty();
		assertValid(document);
	}

	@Test
	public void createDocumentWithRequestIp() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "10.0.0.123");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getRequestIp()).isEqualTo("10.0.0.123");
		assertThat(document.getRequestIpv4()).isEqualTo("10.0.0.123");
		assertThat(document.getRequestCountry()).isNull();
	}

	@Test
	public void createDocumentWithRequestIpv6() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getRequestIp()).isEqualTo("2001:db8:a0b:12f0::1");
		assertThat(document.getRequestIpv4()).isNull();
		assertThat(document.getRequestCountry()).isNull();
	}

	@Test
	public void createDocumentWithCloudFlareHeaders() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("cf-ipcountry", "BE");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getRequestIp()).isEqualTo("10.0.0.123");
		assertThat(document.getRequestIpv4()).isEqualTo("10.0.0.123");
		assertThat(document.getRequestCountry()).isEqualTo("BE");
	}

	@Test
	public void createDocumentWithCloudFlareIpv6() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getRequestIp()).isEqualTo("2001:db8:a0b:12f0::1");
		assertThat(document.getRequestIpv4()).isNull();
		assertThat(document.getRequestCountry()).isNull();
	}

	@Test
	public void createDocumentWithCloudFlareHeadersAndOtherHeaders() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("x-forwarded-for", "192.168.1.101");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getRequestIp()).isEqualTo("10.0.0.123");
		assertThat(document.getRequestIpv4()).isEqualTo("10.0.0.123");
		assertThat(document.getRequestCountry()).isNull();
	}

	@Test
	public void createDocumentWithCloudFlareCountrySetToXX() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "Xx"); // case insensitive
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getRequestCountry()).isNull();
	}

	@Test
	public void createDocumentWithUserAgent() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "HTTPie/0.8.0");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClientId()).isEqualTo("httpie");
		assertThat(document.getClientVersion()).isEqualTo("0.8.0");
	}

	@Test
	public void createDocumentWithUserAgentNoVersion() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "IntelliJ IDEA");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClientId()).isEqualTo("intellijidea");
		assertThat(document.getClientVersion()).isEqualTo(null);
	}

	@Test
	public void createDocumentInvalidJavaVersion() {
		ProjectRequest request = createProjectRequest();
		request.setJavaVersion("1.2");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getJavaVersion()).isEqualTo("1.2");
		assertThat(document.isInvalid()).isTrue();
		assertThat(document.isInvalidJavaVersion()).isTrue();
	}

	@Test
	public void createDocumentInvalidLanguage() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("c++");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getLanguage()).isEqualTo("c++");
		assertThat(document.isInvalid()).isTrue();
		assertThat(document.isInvalidLanguage()).isTrue();
	}

	@Test
	public void createDocumentInvalidPackaging() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("ear");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getPackaging()).isEqualTo("ear");
		assertThat(document.isInvalid()).isTrue();
		assertThat(document.isInvalidPackaging()).isTrue();
	}

	@Test
	public void createDocumentInvalidType() {
		ProjectRequest request = createProjectRequest();
		request.setType("ant-project");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getType()).isEqualTo("ant-project");
		assertThat(document.isInvalid()).isTrue();
		assertThat(document.isInvalidType()).isTrue();
	}

	@Test
	public void createDocumentInvalidDependency() {
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Arrays.asList("web", "invalid", "data-jpa", "invalid-2"));
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getDependencies().get(0)).isEqualTo("web");
		assertThat(document.getDependencies().get(1)).isEqualTo("data-jpa");
		assertThat(document.getDependencies()).hasSize(2);
		assertThat(document.isInvalid()).isTrue();
		assertThat(document.getInvalidDependencies().get(0)).isEqualTo("invalid");
		assertThat(document.getInvalidDependencies().get(1)).isEqualTo("invalid-2");
		assertThat(document.getInvalidDependencies()).hasSize(2);
	}

	@Test
	public void createDocumentWithProjectFailedEvent() {
		ProjectRequest request = createProjectRequest();
		ProjectFailedEvent event = new ProjectFailedEvent(request,
				new IllegalStateException("my test message"));
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.isInvalid()).isTrue();
		assertThat(document.getErrorMessage()).isEqualTo("my test message");
	}

	private static void assertValid(ProjectRequestDocument document) {
		assertThat(document.isInvalid()).isFalse();
		assertThat(document.isInvalidJavaVersion()).isFalse();
		assertThat(document.isInvalidLanguage()).isFalse();
		assertThat(document.isInvalidPackaging()).isFalse();
		assertThat(document.getInvalidDependencies()).isEmpty();
	}

}
