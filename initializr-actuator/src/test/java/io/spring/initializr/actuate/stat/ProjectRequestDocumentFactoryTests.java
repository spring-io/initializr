/*
 * Copyright 2012-2019 the original author or authors.
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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProjectRequestDocumentFactory}.
 *
 * @author Stephane Nicoll
 */
class ProjectRequestDocumentFactoryTests extends AbstractInitializrStatTests {

	private final ProjectRequestDocumentFactory factory = new ProjectRequestDocumentFactory(
			createProvider(getMetadata()));

	@Test
	void createDocumentForSimpleProject() {
		ProjectRequest request = createProjectRequest();
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getArtifactId()).isEqualTo("demo");
		assertThat(document.getBuildSystem()).isEqualTo("maven");
		assertThat(document.getClient()).isNull();
		assertThat(document.getDependencies().getValues()).isEmpty();
		assertThat(document.getDependencies().getId()).isEqualTo("_none");
		assertThat(document.getDependencies().getCount()).isEqualTo(0);
		assertThat(document.getErrorState()).isNull();
		assertThat(document.getGenerationTimestamp()).isEqualTo(event.getTimestamp());
		assertThat(document.getGroupId()).isEqualTo("com.example");
		assertThat(document.getJavaVersion()).isEqualTo("1.8");
		assertThat(document.getLanguage()).isEqualTo("java");
		assertThat(document.getPackageName()).isEqualTo("com.example.demo");
		assertThat(document.getPackaging()).isEqualTo("jar");
		assertThat(document.getType()).isEqualTo("maven-project");
		assertThat(document.getVersion().getId()).isEqualTo("2.1.1.RELEASE");
		assertThat(document.getVersion().getMajor()).isEqualTo("2");
		assertThat(document.getVersion().getMinor()).isEqualTo("2.1");
	}

	@Test
	void createDocumentWithRequestIp() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "10.0.0.123");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getIp()).isEqualTo("10.0.0.123");
		assertThat(document.getClient().getCountry()).isNull();
	}

	@Test
	void createDocumentWithRequestIpv6() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("x-forwarded-for", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getIp()).isEqualTo("2001:db8:a0b:12f0::1");
		assertThat(document.getClient().getCountry()).isNull();
	}

	@Test
	void createDocumentWithCloudFlareHeaders() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("cf-ipcountry", "BE");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getIp()).isEqualTo("10.0.0.123");
		assertThat(document.getClient().getCountry()).isEqualTo("BE");
	}

	@Test
	void createDocumentWithCloudFlareIpv6() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "2001:db8:a0b:12f0::1");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getIp()).isEqualTo("2001:db8:a0b:12f0::1");
		assertThat(document.getClient().getCountry()).isNull();
	}

	@Test
	void createDocumentWithCloudFlareHeadersAndOtherHeaders() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "10.0.0.123");
		request.getParameters().put("x-forwarded-for", "192.168.1.101");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getIp()).isEqualTo("10.0.0.123");
		assertThat(document.getClient().getCountry()).isNull();
	}

	@Test
	void createDocumentWithCloudFlareCountrySetToXX() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("cf-connecting-ip", "Xx"); // case insensitive
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getCountry()).isNull();
	}

	@Test
	void createDocumentWithUserAgent() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "HTTPie/0.8.0");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getId()).isEqualTo("httpie");
		assertThat(document.getClient().getVersion()).isEqualTo("0.8.0");
	}

	@Test
	void createDocumentWithUserAgentNoVersion() {
		ProjectRequest request = createProjectRequest();
		request.getParameters().put("user-agent", "IntelliJ IDEA");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getClient().getId()).isEqualTo("intellijidea");
		assertThat(document.getClient().getVersion()).isNull();
	}

	@Test
	void createDocumentInvalidJavaVersion() {
		ProjectRequest request = createProjectRequest();
		request.setJavaVersion("1.2");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getJavaVersion()).isEqualTo("1.2");
		assertThat(document.getErrorState().isInvalid()).isTrue();
		assertThat(document.getErrorState().getJavaVersion()).isTrue();
		assertThat(document.getErrorState().getLanguage()).isNull();
		assertThat(document.getErrorState().getPackaging()).isNull();
		assertThat(document.getErrorState().getType()).isNull();
		assertThat(document.getErrorState().getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidLanguage() {
		ProjectRequest request = createProjectRequest();
		request.setLanguage("c++");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getLanguage()).isEqualTo("c++");
		assertThat(document.getErrorState().isInvalid()).isTrue();
		assertThat(document.getErrorState().getJavaVersion()).isNull();
		assertThat(document.getErrorState().getLanguage()).isTrue();
		assertThat(document.getErrorState().getPackaging()).isNull();
		assertThat(document.getErrorState().getType()).isNull();
		assertThat(document.getErrorState().getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidPackaging() {
		ProjectRequest request = createProjectRequest();
		request.setPackaging("ear");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getPackaging()).isEqualTo("ear");
		assertThat(document.getErrorState().isInvalid()).isTrue();
		assertThat(document.getErrorState().getJavaVersion()).isNull();
		assertThat(document.getErrorState().getLanguage()).isNull();
		assertThat(document.getErrorState().getPackaging()).isTrue();
		assertThat(document.getErrorState().getType()).isNull();
		assertThat(document.getErrorState().getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidType() {
		ProjectRequest request = createProjectRequest();
		request.setType("ant-project");
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getType()).isEqualTo("ant-project");
		assertThat(document.getErrorState().isInvalid()).isTrue();
		assertThat(document.getErrorState().getJavaVersion()).isNull();
		assertThat(document.getErrorState().getLanguage()).isNull();
		assertThat(document.getErrorState().getPackaging()).isNull();
		assertThat(document.getErrorState().getType()).isTrue();
		assertThat(document.getErrorState().getDependencies()).isNull();
	}

	@Test
	void createDocumentInvalidDependency() {
		ProjectRequest request = createProjectRequest();
		request.setDependencies(Arrays.asList("web", "invalid", "data-jpa", "invalid-2"));
		ProjectGeneratedEvent event = new ProjectGeneratedEvent(request);
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getDependencies().getValues()).containsExactly("web",
				"data-jpa");
		assertThat(document.getErrorState().isInvalid()).isTrue();
		assertThat(document.getErrorState().getJavaVersion()).isNull();
		assertThat(document.getErrorState().getLanguage()).isNull();
		assertThat(document.getErrorState().getPackaging()).isNull();
		assertThat(document.getErrorState().getType()).isNull();
		assertThat(document.getErrorState().getDependencies()).isNotNull();
		assertThat(document.getErrorState().getDependencies().isInvalid()).isTrue();
		assertThat(document.getErrorState().getDependencies().getValues())
				.containsExactly("invalid", "invalid-2");
	}

	@Test
	void createDocumentWithProjectFailedEvent() {
		ProjectRequest request = createProjectRequest();
		ProjectFailedEvent event = new ProjectFailedEvent(request,
				new IllegalStateException("my test message"));
		ProjectRequestDocument document = this.factory.createDocument(event);
		assertThat(document.getErrorState().isInvalid()).isTrue();
		assertThat(document.getErrorState().getJavaVersion()).isNull();
		assertThat(document.getErrorState().getLanguage()).isNull();
		assertThat(document.getErrorState().getPackaging()).isNull();
		assertThat(document.getErrorState().getType()).isNull();
		assertThat(document.getErrorState().getDependencies()).isNull();
		assertThat(document.getErrorState().getMessage()).isEqualTo("my test message");
	}

}
