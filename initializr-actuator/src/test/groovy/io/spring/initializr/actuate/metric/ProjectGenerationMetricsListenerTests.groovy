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

package io.spring.initializr.actuate.metric

import io.spring.initializr.actuate.test.MetricsAssert
import io.spring.initializr.actuate.test.TestCounterService
import io.spring.initializr.generator.ProjectFailedEvent
import io.spring.initializr.generator.ProjectGeneratedEvent
import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.metadata.Dependency
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder
import org.junit.Before
import org.junit.Test

/**
 * @author Stephane Nicoll
 */
class ProjectGenerationMetricsListenerTests {

	private InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
			.addDependencyGroup('core', 'web', 'security', 'spring-data').build()

	private ProjectGenerationMetricsListener listener
	private MetricsAssert metricsAssert


	@Before
	void setup() {
		def counterService = new TestCounterService()
		listener = new ProjectGenerationMetricsListener(counterService)
		metricsAssert = new MetricsAssert(counterService)
	}

	@Test
	void projectGenerationCount() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.requests')
	}

	@Test
	void projectGenerationCountWithFailure() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectFailedEvent(request)
		metricsAssert.hasValue(1, 'initializr.requests')
		metricsAssert.hasValue(1, 'initializr.failures')
	}

	@Test
	void dependencies() {
		def request = initialize()
		request.style << 'security' << 'spring-data'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.dependency.security',
				'initializr.dependency.spring-data')
	}

	@Test
	void noDependencies() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasNoValue('initializr.dependency.')
	}

	@Test
	void resolvedWebDependency() {
		def request = initialize()
		request.style << 'spring-data'
		request.packaging = 'war'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.dependency.web',
				'initializr.dependency.spring-data')
	}

	@Test
	void aliasedDependencyUseStandardId() {
		def dependency = new Dependency()
		dependency.id = 'foo'
		dependency.aliases << 'foo-old'
		def metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup('core', dependency).build()
		def request = new ProjectRequest()
		request.initialize(metadata)
		request.style << 'foo-old'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.dependency.foo') // standard id is used
	}

	@Test
	void defaultType() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.type.maven-project')
	}

	@Test
	void explicitType() {
		def request = initialize()
		request.type = 'gradle-build'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.type.gradle-build')
	}

	@Test
	void defaultPackaging() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.packaging.jar')
	}

	@Test
	void explicitPackaging() {
		def request = initialize()
		request.packaging = 'war'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.packaging.war')
	}

	@Test
	void defaultJavaVersion() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.java_version.1_8')
	}

	@Test
	void explicitJavaVersion() {
		def request = initialize()
		request.javaVersion = '1.7'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.java_version.1_7')
	}

	@Test
	void defaultLanguage() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.language.java')
	}

	@Test
	void explicitGroovyLanguage() {
		def request = initialize()
		request.language = 'groovy'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.language.groovy')
	}

	@Test
	void explicitKotlinLanguage() {
		def request = initialize()
		request.language = 'kotlin'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.language.kotlin')
	}

	@Test
	void defaultBootVersion() {
		def request = initialize()
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.boot_version.1_2_3_RELEASE')
	}

	@Test
	void explicitBootVersion() {
		def request = initialize()
		request.bootVersion = '1.0.2.RELEASE'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.boot_version.1_0_2_RELEASE')
	}

	@Test
	void userAgentAvailable() {
		def request = initialize()
		request.parameters['user-agent'] = 'HTTPie/0.9.2'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.client_id.httpie')
	}

	@Test
	void collectAllMetrics() {
		def request = initialize()
		request.style << 'web' << 'security'
		request.type = 'gradle-project'
		request.packaging = 'jar'
		request.javaVersion = '1.6'
		request.language = 'groovy'
		request.bootVersion = '1.0.2.RELEASE'
		request.parameters['user-agent'] = 'SpringBootCli/1.3.0.RELEASE'

		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.requests',
				'initializr.dependency.web', 'initializr.dependency.security',
				'initializr.type.gradle-project', 'initializr.packaging.jar',
				'initializr.java_version.1_6', 'initializr.language.groovy',
				'initializr.boot_version.1_0_2_RELEASE',
				'initializr.client_id.spring').metricsCount(9)
	}

	@Test
	void incrementMetrics() {
		def request = initialize()
		request.style << 'security' << 'spring-data'
		request.resolve(metadata)
		fireProjectGeneratedEvent(request)
		metricsAssert.hasValue(1, 'initializr.requests',
				'initializr.dependency.security', 'initializr.dependency.spring-data')

		def anotherRequest = initialize()
		anotherRequest.style << 'web' << 'spring-data'
		anotherRequest.resolve(metadata)
		fireProjectGeneratedEvent(anotherRequest)
		metricsAssert.hasValue(2, 'initializr.dependency.spring-data',
				'initializr.dependency.spring-data')
		metricsAssert.hasValue(1, 'initializr.dependency.web',
				'initializr.dependency.security')
	}

	private fireProjectGeneratedEvent(ProjectRequest projectRequest) {
		listener.onGeneratedProject(new ProjectGeneratedEvent(projectRequest))
	}

	private fireProjectFailedEvent(ProjectRequest projectRequest) {
		listener.onFailedProject(new ProjectFailedEvent(projectRequest, null))
	}

	private ProjectRequest initialize() {
		def request = new ProjectRequest()
		request.initialize(metadata)
		request
	}

}
