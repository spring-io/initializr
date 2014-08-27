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
import io.spring.initializr.support.MetricsAssert
import io.spring.initializr.support.TestCounterService
import org.junit.Before
import org.junit.Test

/**
 * @author Stephane Nicoll
 */
class ProjectGenerationMetricsListenerTests {

	private InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
			.addDependencyGroup('core', 'web', 'security', 'spring-data').validateAndGet()

	private ProjectGenerationMetricsListener listener
	private MetricsAssert metricsAssert


	@Before
	public void setup() {
		TestCounterService counterService = new TestCounterService()
		listener = new ProjectGenerationMetricsListener(counterService)
		metricsAssert = new MetricsAssert(counterService)
	}

	@Test
	public void projectGenerationCount() {
		ProjectRequest request = initialize()
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.requests')
	}

	@Test
	public void dependencies() {
		ProjectRequest request = initialize()
		request.style << 'security' << 'spring-data'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.dependency.security',
				'initializr.dependency.spring-data')
	}

	@Test
	public void resolvedWebDependency() {
		ProjectRequest request = initialize()
		request.style << 'spring-data'
		request.packaging = 'war'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.dependency.web',
				'initializr.dependency.spring-data')
	}

	@Test
	public void aliasedDependencyUseStandardId() {
		InitializrMetadata.Dependency dependency = new InitializrMetadata.Dependency()
		dependency.id ='foo'
		dependency.aliases << 'foo-old'
		InitializrMetadata metadata = InitializrMetadataBuilder.withDefaults()
				.addDependencyGroup('core', dependency).validateAndGet()
		ProjectRequest request = new ProjectRequest()
		metadata.initializeProjectRequest(request)
		request.style << 'foo-old'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.dependency.foo') // standard id is used
	}

	@Test
	public void defaultType() {
		ProjectRequest request = initialize()
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.type.starter_zip')
	}

	@Test
	public void explicitType() {
		ProjectRequest request = initialize()
		request.type = 'build.gradle'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.type.build_gradle')
	}

	@Test
	public void defaultPackaging() {
		ProjectRequest request = initialize()
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.packaging.jar')
	}

	@Test
	public void explicitPackaging() {
		ProjectRequest request = initialize()
		request.packaging = 'war'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.packaging.war')
	}

	@Test
	public void defaultJavaVersion() {
		ProjectRequest request = initialize()
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.java_version.1_7')
	}

	@Test
	public void explicitJavaVersion() {
		ProjectRequest request = initialize()
		request.javaVersion = '1.8'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.java_version.1_8')
	}

	@Test
	public void defaultLanguage() {
		ProjectRequest request = initialize()
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.language.java')
	}

	@Test
	public void explicitLanguage() {
		ProjectRequest request = initialize()
		request.language = 'groovy'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.language.groovy')
	}

	@Test
	public void defaultBootVersion() {
		ProjectRequest request = initialize()
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.boot_version.1_1_5_RELEASE')
	}

	@Test
	public void explicitBootVersion() {
		ProjectRequest request = initialize()
		request.bootVersion = '1.0.2.RELEASE'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.boot_version.1_0_2_RELEASE')
	}

	@Test
	public void collectAllMetrics() {
		ProjectRequest request = initialize()
		request.style << 'web' << 'security'
		request.type = 'gradle.zip'
		request.packaging = 'jar'
		request.javaVersion = '1.6'
		request.language = 'groovy'
		request.bootVersion = '1.0.2.RELEASE'

		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.requests',
				'initializr.dependency.web', 'initializr.dependency.security',
				'initializr.type.gradle_zip', 'initializr.packaging.jar',
				'initializr.java_version.1_6', 'initializr.language.groovy',
				'initializr.boot_version.1_0_2_RELEASE').metricsCount(8)
	}

	@Test
	public void incrementMetrics() {
		ProjectRequest request = initialize()
		request.style << 'security' << 'spring-data'
		request.resolve(metadata)
		listener.onGeneratedProject(request)
		metricsAssert.hasValue(1, 'initializr.requests',
				'initializr.dependency.security', 'initializr.dependency.spring-data')

		ProjectRequest anotherRequest = initialize()
		anotherRequest.style << 'web' << 'spring-data'
		anotherRequest.resolve(metadata)
		listener.onGeneratedProject(anotherRequest)
		metricsAssert.hasValue(2, 'initializr.dependency.spring-data',
				'initializr.dependency.spring-data')
		metricsAssert.hasValue(1, 'initializr.dependency.web',
				'initializr.dependency.security')
	}

	private ProjectRequest initialize() {
		ProjectRequest request = new ProjectRequest()
		metadata.initializeProjectRequest(request)
		request
	}

}
