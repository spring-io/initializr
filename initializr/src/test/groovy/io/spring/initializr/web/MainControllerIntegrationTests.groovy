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

package io.spring.initializr.web

import java.nio.charset.Charset

import io.spring.initializr.support.ProjectAssert
import org.json.JSONObject
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.StreamUtils

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void simpleZipProject() {
		downloadZip('/starter.zip?style=web&style=jpa').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa') // alias jpa -> data-jpa
				.hasSpringBootStarterDependency('test')
	}

	@Test
	void simpleTgzProject() {
		downloadTgz('/starter.tgz?style=org.acme:bar').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasDependency('org.acme', 'bar', '2.1.0')
	}

	@Test
	void gradleWarProject() {
		downloadZip('/starter.zip?style=web&style=security&packaging=war&type=gradle.zip')
				.isJavaWarProject().isGradleProject()
	}

	@Test
	void downloadCli() {
		assertSpringCliRedirect('/spring', 'zip')
	}

	@Test
	void downloadCliAsZip() {
		assertSpringCliRedirect('/spring.zip', 'zip')
	}

	@Test
	void downloadCliAsTarGz() {
		assertSpringCliRedirect('/spring.tar.gz', 'tar.gz')
	}

	@Test
	void downloadCliAsTgz() {
		assertSpringCliRedirect('/spring.tgz', 'tar.gz')
	}

	private void assertSpringCliRedirect(String context, String extension) {
		ResponseEntity<?> entity = restTemplate.getForEntity(createUrl(context), ResponseEntity.class)
		assertEquals HttpStatus.FOUND, entity.getStatusCode()
		assertEquals new URI('https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/1.1.4.RELEASE' +
				'/spring-boot-cli-1.1.4.RELEASE-bin.'+extension), entity.getHeaders().getLocation()

	}

	@Test // Test that the current output is exactly what we expect
	void validateCurrentProjectMetadata() {
		String json = restTemplate.getForObject(createUrl('/'), String.class)
		JSONObject expected = readJson('1.0')
		JSONAssert.assertEquals(expected, new JSONObject(json), JSONCompareMode.STRICT)
	}

	@Test // Test that the  current code complies "at least" with 1.0
	void validateProjectMetadata10() {
		String json = restTemplate.getForObject(createUrl('/'), String.class)
		JSONObject expected = readJson('1.0')
		JSONAssert.assertEquals(expected, new JSONObject(json), JSONCompareMode.LENIENT)
	}

	// Existing tests for backward compatibility

	@Test
	void homeIsForm() {
		String body = htmlHome()
		assertTrue 'Wrong body:\n' + body, body.contains('action="/starter.zip"')
	}

	@Test
	void homeIsJson() {
		String body = restTemplate.getForObject(createUrl('/'), String)
		assertTrue('Wrong body:\n' + body, body.contains('{"dependencies"'))
	}

	@Test
	void webIsAddedPom() {
		String body = restTemplate.getForObject(createUrl('/pom.xml?packaging=war'), String)
		assertTrue('Wrong body:\n' + body, body.contains('spring-boot-starter-web'))
		assertTrue('Wrong body:\n' + body, body.contains('provided'))
	}

	@Test
	void webIsAddedGradle() {
		String body = restTemplate.getForObject(createUrl('/build.gradle?packaging=war'), String)
		assertTrue('Wrong body:\n' + body, body.contains('spring-boot-starter-web'))
		assertTrue('Wrong body:\n' + body, body.contains('providedRuntime'))
	}

	@Test
	void infoHasExternalProperties() {
		String body = restTemplate.getForObject(createUrl('/info'), String)
		assertTrue('Wrong body:\n' + body, body.contains('"spring-boot"'))
		assertTrue('Wrong body:\n' + body, body.contains('"version":"1.1.4.RELEASE"'))
	}

	@Test
	void homeHasWebStyle() {
		String body = htmlHome()
		assertTrue('Wrong body:\n' + body, body.contains('name="style" value="web"'))
	}

	@Test
	void homeHasBootVersion() {
		String body = htmlHome()
		assertTrue('Wrong body:\n' + body, body.contains('name="bootVersion"'))
		assertTrue('Wrong body:\n' + body, body.contains('1.2.0.BUILD-SNAPSHOT"'))
	}

	@Test
	void downloadStarter() {
		byte[] body = restTemplate.getForObject(createUrl('starter.zip'), byte[])
		assertNotNull(body)
		assertTrue(body.length > 100)
	}

	@Test
	void installer() {
		ResponseEntity<String> response = restTemplate.getForEntity(createUrl('install.sh'), String)
		assertEquals(HttpStatus.OK, response.getStatusCode())
		assertNotNull(response.body)
	}


	private ProjectAssert downloadZip(String context) {
		byte[] body = restTemplate.getForObject(createUrl(context), byte[])
		zipProjectAssert(body)
	}

	private ProjectAssert downloadTgz(String context) {
		byte[] body = restTemplate.getForObject(createUrl(context), byte[])
		tgzProjectAssert(body)
	}

	private static JSONObject readJson(String version) {
		def resource = new ClassPathResource('metadata/test-default-' + version + '.json')
		def stream = resource.getInputStream()
		try {
			String json = StreamUtils.copyToString(stream, Charset.forName('UTF-8'))
			new JSONObject(json)
		} finally {
			stream.close()
		}
	}

}
