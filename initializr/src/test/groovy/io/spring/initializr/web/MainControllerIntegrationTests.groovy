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

import groovy.json.JsonSlurper
import io.spring.initializr.support.ProjectAssert
import org.json.JSONObject
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.StreamUtils
import org.springframework.web.client.HttpClientErrorException

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	private static final MediaType CURRENT_METADATA_MEDIA_TYPE =
			MediaType.parseMediaType('application/vnd.initializr.v2+json')

	private final def slurper = new JsonSlurper()

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
	void noDependencyProject() {
		downloadZip('/starter.zip').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasSpringBootStarterRootDependency() // the root dep is added if none is specified
				.hasSpringBootStarterDependency('test')
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
		def entity = restTemplate.getForEntity(createUrl(context), ResponseEntity.class)
		assertEquals HttpStatus.FOUND, entity.getStatusCode()
		def expected = "https://repo.spring.io/release/org/springframework/boot/spring-boot-cli/1.1.4.RELEASE/spring-boot-cli-1.1.4.RELEASE-bin.$extension"
		assertEquals new URI(expected), entity.getHeaders().getLocation()

	}

	@Test
	void metadataWithNoAcceptHeader() { //  rest template sets application/json by default
		ResponseEntity<String> response = getMetadata(null, '*/*')
		assertEquals CURRENT_METADATA_MEDIA_TYPE, response.getHeaders().getContentType()
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test
	void metadataWithCurrentAcceptHeader() {
		ResponseEntity<String> response = getMetadata(null, 'application/vnd.initializr.v2+json')
		assertEquals CURRENT_METADATA_MEDIA_TYPE, response.getHeaders().getContentType()
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test // Test that the current output is exactly what we expect
	void validateCurrentProjectMetadata() {
		def json = getMetadataJson()
		validateCurrentMetadata(json)
	}

	private void validateCurrentMetadata(JSONObject json) {
		def expected = readJson('2.0.0')
		JSONAssert.assertEquals(expected, json, JSONCompareMode.STRICT)
	}

	@Test // Test that the  current code complies exactly with 1.1.0
	void validateProjectMetadata110() {
		JSONObject json = getMetadataJson("SpringBootCli/1.2.0.RC1", null)
		def expected = readJson('1.0.1')
		JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT)
	}

	@Test // Test that the  current code complies "at least" with 1.0.1
	void validateProjectMetadata101() {
		JSONObject json = getMetadataJson("SpringBootCli/1.2.0.RC1", null)
		def expected = readJson('1.0.1')
		JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT)
	}

	@Test // Test that the  current code complies "at least" with 1.0.0
	void validateProjectMetadata100() {
		JSONObject json = getMetadataJson("SpringBootCli/1.2.0.RC1", null)
		def expected = readJson('1.0.0')
		JSONAssert.assertEquals(expected, json, JSONCompareMode.LENIENT)
	}

	@Test
	void metricsAvailableByDefault() {
		downloadZip('/starter.zip?packaging=jar&javaVersion=1.8&style=web&style=jpa')
		def result = metricsEndpoint()
		def requests = result['counter.initializr.requests']
		def packaging = result['counter.initializr.packaging.jar']
		def javaVersion = result['counter.initializr.java_version.1_8']
		def webDependency = result['counter.initializr.dependency.web']
		def jpaDependency = result['counter.initializr.dependency.jpa']

		downloadZip('/starter.zip?packaging=jar&javaVersion=1.8&style=web') // No jpa dep this time

		def updatedResult = metricsEndpoint()
		assertEquals 'Number of request should have increased',
				requests + 1, updatedResult['counter.initializr.requests']
		assertEquals 'jar packaging metric should have increased',
				packaging + 1, updatedResult['counter.initializr.packaging.jar']
		assertEquals 'java version metric should have increased',
				javaVersion + 1, updatedResult['counter.initializr.java_version.1_8']
		assertEquals 'web dependency metric should have increased',
				webDependency + 1, updatedResult['counter.initializr.dependency.web']
		assertEquals 'jpa dependency metric should not have increased',
				jpaDependency, updatedResult['counter.initializr.dependency.jpa']
	}

	private def metricsEndpoint() {
		parseJson(restTemplate.getForObject(createUrl('/metrics'), String))
	}

	@Test
	void missingDependencyProperException() {
		try {
			invoke('/starter.zip?style=foo:bar')
		} catch (HttpClientErrorException ex) {
			def error = parseJson(ex.responseBodyAsString)
			assertEquals HttpStatus.BAD_REQUEST, ex.getStatusCode()
			assertTrue 'Dependency not in error message', error.message.contains('foo:bar')
		}

	}

	private def parseJson(String content) {
		slurper.parseText(content)
	}

	@Test
	void downloadWithUnknownSpringBootStarter() { // Simple id are accepted as spring-boot-starter
		 downloadZip('/starter.zip?style=foo').pomAssert().hasSpringBootStarterDependency('foo')
	}

	// Existing tests for backward compatibility

	@Test
	void homeIsForm() {
		def body = htmlHome()
		assertTrue "Wrong body:\n$body", body.contains('action="/starter.zip"')
	}

	@Test
	void homeIsJson() {
		def body = restTemplate.getForObject(createUrl('/'), String)
		assertTrue("Wrong body:\n$body", body.contains('"dependencies"'))
	}

	@Test
	void webIsAddedPom() {
		def body = restTemplate.getForObject(createUrl('/pom.xml?packaging=war'), String)
		assertTrue("Wrong body:\n$body", body.contains('spring-boot-starter-web'))
		assertTrue("Wrong body:\n$body", body.contains('provided'))
	}

	@Test
	void webIsAddedGradle() {
		def body = restTemplate.getForObject(createUrl('/build.gradle?packaging=war'), String)
		assertTrue("Wrong body:\n$body", body.contains('spring-boot-starter-web'))
		assertTrue("Wrong body:\n$body", body.contains('providedRuntime'))
	}

	@Test
	void infoHasExternalProperties() {
		def body = restTemplate.getForObject(createUrl('/info'), String)
		assertTrue("Wrong body:\n$body", body.contains('"spring-boot"'))
		assertTrue("Wrong body:\n$body", body.contains('"version":"1.1.4.RELEASE"'))
	}

	@Test
	void homeHasWebStyle() {
		def body = htmlHome()
		assertTrue("Wrong body:\n$body", body.contains('name="style" value="web"'))
	}

	@Test
	void homeHasBootVersion() {
		def body = htmlHome()
		assertTrue("Wrong body:\n$body", body.contains('name="bootVersion"'))
		assertTrue("Wrong body:\n$body", body.contains('1.2.0.BUILD-SNAPSHOT"'))
	}

	@Test
	void downloadStarter() {
		def body = restTemplate.getForObject(createUrl('starter.zip'), byte[])
		assertNotNull(body)
		assertTrue(body.length > 100)
	}

	@Test
	void installer() {
		def response = restTemplate.getForEntity(createUrl('install.sh'), String)
		assertEquals(HttpStatus.OK, response.getStatusCode())
		assertNotNull(response.body)
	}

	private JSONObject getMetadataJson()  {
		getMetadataJson(null, null)
	}

	private JSONObject getMetadataJson(String userAgentHeader, String acceptHeader) {
		String json = getMetadata(userAgentHeader, acceptHeader).body
		return new JSONObject(json)
	}

	private ResponseEntity<String> getMetadata(String userAgentHeader, String acceptHeader) {
		HttpHeaders headers = new HttpHeaders();
		if (userAgentHeader) {
			headers.set("User-Agent", userAgentHeader);
		}
		if (acceptHeader) {
			headers.setAccept(Collections.singletonList(MediaType.parseMediaType(acceptHeader)))
		}
		return restTemplate.exchange(createUrl('/'),
				HttpMethod.GET, new HttpEntity<Void>(headers), String.class)
	}


	private byte[] invoke(String context) {
		restTemplate.getForObject(createUrl(context), byte[])
	}

	private ProjectAssert downloadZip(String context) {
		def body = invoke(context)
		zipProjectAssert(body)
	}

	private ProjectAssert downloadTgz(String context) {
		def body = invoke(context)
		tgzProjectAssert(body)
	}

	private JSONObject readJson(String version) {
		def resource = new ClassPathResource("metadata/test-default-$version" + ".json")
		def stream = resource.inputStream
		try {
			def json = StreamUtils.copyToString(stream, Charset.forName('UTF-8'))

			// Let's parse the port as it is random
			def content = json.replaceAll('@port@', String.valueOf(this.port))
			new JSONObject(content)
		} finally {
			stream.close()
		}
	}

}
