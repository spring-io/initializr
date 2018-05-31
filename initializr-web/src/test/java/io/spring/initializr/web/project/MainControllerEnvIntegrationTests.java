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

package io.spring.initializr.web.project;

import java.net.URI;

import io.spring.initializr.test.generator.ProjectAssert;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.junit.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-custom-env" })
public class MainControllerEnvIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	public void downloadCliWithCustomRepository() throws Exception {
		ResponseEntity<?> entity = getRestTemplate().getForEntity(createUrl("/spring"),
				String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		String expected = "https://repo.spring.io/lib-release/org/springframework/boot/spring-boot-cli/1.1.4.RELEASE/spring-boot-cli-1.1.4.RELEASE-bin.zip";
		assertThat(entity.getHeaders().getLocation()).isEqualTo(new URI(expected));
	}

	@Test
	public void doNotForceSsl() {
		ResponseEntity<String> response = invokeHome("curl/1.2.4", "*/*");
		String body = response.getBody();
		assertThat(body).as("Must not force https").contains("http://start.spring.io/");
		assertThat(body).as("Must not force https").doesNotContain("https://");
	}

	@Test
	public void generateProjectWithInvalidName() {
		downloadZip("/starter.zip?style=data-jpa&name=Invalid")
				.isJavaProject(ProjectAssert.DEFAULT_PACKAGE_NAME, "FooBarApplication")
				.isMavenProject().hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2).hasSpringBootStarterDependency("data-jpa")
				.hasSpringBootStarterTest();
	}

	@Test
	public void googleAnalytics() {
		String body = htmlHome();
		assertThat(body).contains("https://www.googletagmanager.com/gtm.js");
	}

}
