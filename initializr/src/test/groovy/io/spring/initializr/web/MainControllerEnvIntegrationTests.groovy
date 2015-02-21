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

package io.spring.initializr.web

import org.junit.Test

import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles(['test-default', 'test-custom-env'])
class MainControllerEnvIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void downloadCliWithCustomRepository() {
		def entity = restTemplate.getForEntity(createUrl('/spring'), HttpEntity.class)
		assertEquals HttpStatus.FOUND, entity.getStatusCode()
		def expected = "https://repo.spring.io/lib-release/org/springframework/boot/spring-boot-cli/1.1.4.RELEASE/spring-boot-cli-1.1.4.RELEASE-bin.zip"
		assertEquals new URI(expected), entity.getHeaders().getLocation()
	}

	@Test
	void doNotForceSsl() {
		def body = htmlHome()
		assertTrue "Force SSL should be disabled", body.contains("http://localhost:$port/install.sh")
	}

	@Test
	void generateProjectWithInvalidName() {
		downloadZip('/starter.zip?style=data-jpa&name=Invalid')
				.isJavaProject('FooBarApplication')
				.isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasSpringBootStarterDependency('data-jpa')
				.hasSpringBootStarterDependency('test')
	}

}
