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

package io.spring.initializr.actuate.metric;

import java.util.Arrays;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.spring.initializr.actuate.test.MetricsAssert;
import io.spring.initializr.generator.ProjectFailedEvent;
import io.spring.initializr.generator.ProjectGeneratedEvent;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Stephane Nicoll
 */
class ProjectGenerationMetricsListenerTests {

	private InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup("core", "web", "security", "spring-data").build();

	private ProjectGenerationMetricsListener listener;

	private MetricsAssert metricsAssert;

	@BeforeEach
	public void setup() {
		SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry();
		this.listener = new ProjectGenerationMetricsListener(meterRegistry);
		this.metricsAssert = new MetricsAssert(meterRegistry);
	}

	@Test
	void projectGenerationCount() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.requests");
	}

	@Test
	void projectGenerationCountWithFailure() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectFailedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.requests");
		this.metricsAssert.hasValue(1, "initializr.failures");
	}

	@Test
	void dependencies() {
		ProjectRequest request = initialize();
		request.getStyle().addAll(Arrays.asList("security", "spring-data"));
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.dependency.security",
				"initializr.dependency.spring-data");
	}

	@Test
	void noDependencies() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasNoValue("initializr.dependency.");
	}

	@Test
	void resolvedWebDependency() {
		ProjectRequest request = initialize();
		request.getStyle().add("spring-data");
		request.setPackaging("war");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.dependency.web",
				"initializr.dependency.spring-data");
	}

	@Test
	void aliasedDependencyUseStandardId() {
		Dependency dependency = new Dependency();
		dependency.setId("foo");
		dependency.getAliases().add("foo-old");
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("core", dependency).build();
		ProjectRequest request = new ProjectRequest();
		request.initialize(metadata);
		request.getStyle().add("foo-old");
		request.resolve(metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.dependency.foo"); // standard id is
																		// used
	}

	@Test
	void defaultType() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.type.maven-project");
	}

	@Test
	void explicitType() {
		ProjectRequest request = initialize();
		request.setType("gradle-build");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.type.gradle-build");
	}

	@Test
	void defaultPackaging() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.packaging.jar");
	}

	@Test
	void explicitPackaging() {
		ProjectRequest request = initialize();
		request.setPackaging("war");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.packaging.war");
	}

	@Test
	void defaultJavaVersion() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.java_version.1_8");
	}

	@Test
	void explicitJavaVersion() {
		ProjectRequest request = initialize();
		request.setJavaVersion("1.7");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.java_version.1_7");
	}

	@Test
	void defaultLanguage() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.language.java");
	}

	@Test
	void explicitGroovyLanguage() {
		ProjectRequest request = initialize();
		request.setLanguage("groovy");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.language.groovy");
	}

	@Test
	void explicitKotlinLanguage() {
		ProjectRequest request = initialize();
		request.setLanguage("kotlin");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.language.kotlin");
	}

	@Test
	void defaultBootVersion() {
		ProjectRequest request = initialize();
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.boot_version.2_1_1_RELEASE");
	}

	@Test
	void explicitBootVersion() {
		ProjectRequest request = initialize();
		request.setBootVersion("1.5.17.RELEASE");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.boot_version.1_5_17_RELEASE");
	}

	@Test
	void userAgentAvailable() {
		ProjectRequest request = initialize();
		request.getParameters().put("user-agent", "HTTPie/0.9.2");
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.client_id.httpie");
	}

	@Test
	void collectAllMetrics() {
		ProjectRequest request = initialize();
		request.getStyle().addAll(Arrays.asList("web", "security"));
		request.setType("gradle-project");
		request.setPackaging("jar");
		request.setJavaVersion("1.6");
		request.setLanguage("groovy");
		request.setBootVersion("1.5.17.RELEASE");
		request.getParameters().put("user-agent", "SpringBootCli/1.3.0.RELEASE");

		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.requests", "initializr.dependency.web",
				"initializr.dependency.security", "initializr.type.gradle-project",
				"initializr.packaging.jar", "initializr.java_version.1_6",
				"initializr.language.groovy", "initializr.boot_version.1_5_17_RELEASE",
				"initializr.client_id.spring").metricsCount(9);
	}

	@Test
	void incrementMetrics() {
		ProjectRequest request = initialize();
		request.getStyle().addAll(Arrays.asList("security", "spring-data"));
		request.resolve(this.metadata);
		fireProjectGeneratedEvent(request);
		this.metricsAssert.hasValue(1, "initializr.requests",
				"initializr.dependency.security", "initializr.dependency.spring-data");

		ProjectRequest anotherRequest = initialize();
		anotherRequest.getStyle().addAll(Arrays.asList("web", "spring-data"));
		anotherRequest.resolve(this.metadata);
		fireProjectGeneratedEvent(anotherRequest);
		this.metricsAssert.hasValue(2, "initializr.dependency.spring-data",
				"initializr.dependency.spring-data");
		this.metricsAssert.hasValue(1, "initializr.dependency.web",
				"initializr.dependency.security");
	}

	private void fireProjectGeneratedEvent(ProjectRequest projectRequest) {
		this.listener.onGeneratedProject(new ProjectGeneratedEvent(projectRequest));
	}

	private void fireProjectFailedEvent(ProjectRequest projectRequest) {
		this.listener.onFailedProject(new ProjectFailedEvent(projectRequest, null));
	}

	private ProjectRequest initialize() {
		ProjectRequest request = new ProjectRequest();
		request.initialize(this.metadata);
		return request;
	}

}
