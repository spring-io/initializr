/*
 * Copyright 2012 - present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.actuate.stat;

import java.util.Arrays;

import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectRequestDocumentFactory}.
 *
 * @author Stephane Nicoll
 */
class ProjectRequestDocumentFactoryTests {

	private final InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
		.addDependencyGroup("core", "security", "validation", "aop")
		.addDependencyGroup("web", "web", "data-rest", "jersey")
		.addDependencyGroup("data", "data-jpa", "jdbc")
		.addDependencyGroup("database", "h2", "mysql")
		.build();

	private final ProjectRequestDocumentFactory factory = new ProjectRequestDocumentFactory();

	@Test
	void createDocumentForSimpleProject() {
		ProjectRequest request = createProjectRequest();
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getArtifactId()).isEqualTo("demo");
		assertThat(document.getBuildSystem()).isEqualTo("maven");
		assertThat(document.getClient()).isNull();
		ProjectRequestDocument.DependencyInformation dependencies = document.getDependencies();
		assertThat(dependencies).isNotNull();
		assertThat(dependencies.getValues()).isEmpty();
		assertThat(dependencies.getId()).isEqualTo("_none");
		assertThat(dependencies.getCount()).isEqualTo(0);
		assertThat(document.getErrorState()).isNull();
		assertThat(document.getGenerationTimestamp()).isEqualTo(event.getTimestamp());
		assertThat(document.getGroupId()).isEqualTo("com.example");
		assertThat(document.getJavaVersion()).isEqualTo("1.8");
		assertThat(document.getLanguage()).isEqualTo("java");
		assertThat(document.getPackageName()).isEqualTo("com.example.demo");
		assertThat(document.getPackaging()).isEqualTo("jar");
		assertThat(document.getType()).isEqualTo("maven-project");
		ProjectRequestDocument.VersionInformation version = document.getVersion();
		assertThat(version).isNotNull();
		assertThat(version.getId()).isEqualTo("2.4.1");
		assertThat(version.getMajor()).isEqualTo("2");
		assertThat(version.getMinor()).isEqualTo("2.4");
	}

	@Test
	void createDocumentWithNonWebProjectRequest() {
		ProjectRequest request = new ProjectRequest();
		request.setBootVersion("2.1.0.RELEASE");
		request.setType("maven-build");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient()).isNull();
	}

	@Test
	void createDocumentWithRequestIp() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "10.0.0.123");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getIp()).isEqualTo("10.0.0.123");
		assertThat(client.getCountry()).isNull();
	}

	@Test
	void createDocumentWithRequestIpv6() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getIp()).isEqualTo("2001:db8:a0b:12f0::1");
		assertThat(client.getCountry()).isNull();
	}

	@Test
	void createDocumentWithCloudFlareHeaders() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("cf-ipcountry", "BE");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getIp()).isEqualTo("10.0.0.123");
		assertThat(client.getCountry()).isEqualTo("BE");
	}

	@Test
	void createDocumentWithCloudFlareIpv6() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getIp()).isEqualTo("2001:db8:a0b:12f0::1");
		assertThat(client.getCountry()).isNull();
	}

	@Test
	void createDocumentWithCloudFlareHeadersAndOtherHeaders() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("x-forwarded-for", "192.168.1.101");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getIp()).isEqualTo("10.0.0.123");
		assertThat(client.getCountry()).isNull();
	}

	@Test
	void createDocumentWithCloudFlareCountrySetToXX() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "Xx"); // case insensitive
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getCountry()).isNull();
	}

	@Test
	void createDocumentWithUserAgent() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "HTTPie/0.8.0");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getId()).isEqualTo("httpie");
		assertThat(client.getVersion()).isEqualTo("0.8.0");
	}

	@Test
	void createDocumentWithUserAgentNoVersion() {
		WebProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "IntelliJ IDEA");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ClientInformation client = document.getClient();
		assertThat(client).isNotNull();
		assertThat(client.getId()).isEqualTo("intellijidea");
		assertThat(client.getVersion()).isNull();
	}

	@Test
	void createDocumentInvalidJavaVersion() {
		ProjectRequest request = createProjectRequest();
		request.setJavaVersion("1.2");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getJavaVersion()).isEqualTo("1.2");
		ProjectRequestDocument.ErrorStateInformation errorState = document.getErrorState();
		assertThat(errorState).isNotNull();
		assertThat(errorState.isInvalid()).isTrue();
		assertThat(errorState.getJavaVersion()).isTrue();
		assertThat(errorState.getLanguage()).isNull();
		assertThat(errorState.getPackaging()).isNull();
		assertThat(errorState.getType()).isNull();
		assertThat(errorState.getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidLanguage() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("c++");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getLanguage()).isEqualTo("c++");
		ProjectRequestDocument.ErrorStateInformation errorState = document.getErrorState();
		assertThat(errorState).isNotNull();
		assertThat(errorState.isInvalid()).isTrue();
		assertThat(errorState.getJavaVersion()).isNull();
		assertThat(errorState.getLanguage()).isTrue();
		assertThat(errorState.getPackaging()).isNull();
		assertThat(errorState.getType()).isNull();
		assertThat(errorState.getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidPackaging() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("ear");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getPackaging()).isEqualTo("ear");
		ProjectRequestDocument.ErrorStateInformation errorState = document.getErrorState();
		assertThat(errorState).isNotNull();
		assertThat(errorState.isInvalid()).isTrue();
		assertThat(errorState.getJavaVersion()).isNull();
		assertThat(errorState.getLanguage()).isNull();
		assertThat(errorState.getPackaging()).isTrue();
		assertThat(errorState.getType()).isNull();
		assertThat(errorState.getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidType() {
		ProjectRequest request = createProjectRequest();
		request.setType("ant-project");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getType()).isEqualTo("ant-project");
		ProjectRequestDocument.ErrorStateInformation errorState = document.getErrorState();
		assertThat(errorState).isNotNull();
		assertThat(errorState.isInvalid()).isTrue();
		assertThat(errorState.getJavaVersion()).isNull();
		assertThat(errorState.getLanguage()).isNull();
		assertThat(errorState.getPackaging()).isNull();
		assertThat(errorState.getType()).isTrue();
		assertThat(errorState.getDependencies()).isNull();
	}

	@Test
	void createDocumentExtendedType() {
		ProjectRequest request = createProjectRequest();
		request.setType("gradle-project-kotlin");
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getType()).isEqualTo("gradle-project-kotlin");
		assertThat(document.getBuildSystem()).isEqualTo("gradle");
	}

	@Test
	void createDocumentInvalidDependency() {
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Arrays.asList("web", "invalid", "data-jpa", "invalid-2"));
		ProjectGeneratedEvent event = createProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.DependencyInformation dependencies = document.getDependencies();
		assertThat(dependencies).isNotNull();
		assertThat(dependencies.getValues()).containsExactly("web", "data-jpa");
		ProjectRequestDocument.ErrorStateInformation errorState = document.getErrorState();
		assertThat(errorState).isNotNull();
		assertThat(errorState.isInvalid()).isTrue();
		assertThat(errorState.getJavaVersion()).isNull();
		assertThat(errorState.getLanguage()).isNull();
		assertThat(errorState.getPackaging()).isNull();
		assertThat(errorState.getType()).isNull();
		assertThat(errorState.getDependencies()).isNotNull();
		assertThat(errorState.getDependencies().isInvalid()).isTrue();
		assertThat(errorState.getDependencies().getValues()).containsExactly("invalid", "invalid-2");
	}

	@Test
	void createDocumentWithProjectFailedEvent() {
		ProjectRequest request = createProjectRequest();
		ProjectFailedEvent event = new ProjectFailedEvent(request, this.metadata,
				new IllegalStateException("my test message"));
		ProjectRequestDocument document = this.factory.createDocument(event);
		ProjectRequestDocument.ErrorStateInformation errorState = document.getErrorState();
		assertThat(errorState).isNotNull();
		assertThat(errorState.isInvalid()).isTrue();
		assertThat(errorState.getJavaVersion()).isNull();
		assertThat(errorState.getLanguage()).isNull();
		assertThat(errorState.getPackaging()).isNull();
		assertThat(errorState.getType()).isNull();
		assertThat(errorState.getDependencies()).isNull();
		assertThat(errorState.getMessage()).isEqualTo("my test message");
	}

	private WebProjectRequest createProjectRequest() {
		WebProjectRequest request = new WebProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

	private ProjectGeneratedEvent createProjectGeneratedEvent(ProjectRequest request) {
		return new ProjectGeneratedEvent(request, this.metadata);
	}

}
