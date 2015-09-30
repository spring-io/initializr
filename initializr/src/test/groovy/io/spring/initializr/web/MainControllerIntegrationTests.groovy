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

import groovy.json.JsonSlurper
import io.spring.initializr.mapper.InitializrMetadataVersion
import io.spring.initializr.metadata.Dependency
import org.json.JSONObject
import org.junit.Ignore
import org.junit.Test
import org.skyscreamer.jsonassert.JSONCompareMode

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.HttpClientErrorException

import static org.hamcrest.CoreMatchers.allOf
import static org.hamcrest.CoreMatchers.containsString
import static org.hamcrest.core.IsNot.not
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {


	private final def slurper = new JsonSlurper()

	@Test
	void simpleZipProject() {
		downloadZip('/starter.zip?style=web&style=jpa').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa') // alias jpa -> data-jpa
				.hasSpringBootStarterTest()
	}

	@Test
	void simpleTgzProject() {
		downloadTgz('/starter.tgz?style=org.acme:foo').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasDependency('org.acme', 'foo', '1.3.5')
	}

	@Test
	void dependencyInRange() {
		def biz = new Dependency(id: 'biz', groupId: 'org.acme',
				artifactId: 'biz', version: '1.3.5', scope: 'runtime')
		downloadTgz('/starter.tgz?style=org.acme:biz&bootVersion=1.2.1.RELEASE').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasDependency(biz)
	}

	@Test
	void dependencyNotInRange() {
		try {
			execute('/starter.tgz?style=org.acme:bur', byte[], null, null)
		} catch (HttpClientErrorException ex) {
			assertEquals HttpStatus.NOT_ACCEPTABLE, ex.statusCode
		}
	}

	@Test
	void noDependencyProject() {
		downloadZip('/starter.zip').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasSpringBootStarterRootDependency() // the root dep is added if none is specified
				.hasSpringBootStarterTest()
	}

	@Test
	void dependenciesIsAnAliasOfStyle() {
		downloadZip('/starter.zip?dependencies=web&dependencies=jpa').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa') // alias jpa -> data-jpa
				.hasSpringBootStarterTest()
	}

	@Test
	void dependenciesIsAnAliasOfStyleCommaSeparated() {
		downloadZip('/starter.zip?dependencies=web,jpa').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(true).pomAssert()
				.hasDependenciesCount(3)
				.hasSpringBootStarterDependency('web')
				.hasSpringBootStarterDependency('data-jpa') // alias jpa -> data-jpa
				.hasSpringBootStarterTest()
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
		ResponseEntity<String> response = invokeHome(null, '*/*')
		validateCurrentMetadata(response)
	}

	@Test
	@Ignore("Need a comparator that does not care about the number of elements in an array")
	void currentMetadataCompatibleWithV2() {
		ResponseEntity<String> response = invokeHome(null, '*/*')
		validateMetadata(response, CURRENT_METADATA_MEDIA_TYPE, '2.0.0', JSONCompareMode.LENIENT)
	}

	@Test
	void metadataWithV2AcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, 'application/vnd.initializr.v2+json')
		validateMetadata(response, InitializrMetadataVersion.V2.mediaType, '2.0.0', JSONCompareMode.STRICT)
	}

	@Test
	void metadataWithCurrentAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null, 'application/vnd.initializr.v2.1+json')
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE)
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test
	void metadataWithSeveralAcceptHeader() {
		ResponseEntity<String> response = invokeHome(null,
				'application/vnd.initializr.v2.1+json', 'application/vnd.initializr.v2+json')
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE)
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test
	void metadataWithUnknownAcceptHeader() {
		try {
			invokeHome(null, 'application/vnd.initializr.v5.4+json')
		} catch (HttpClientErrorException ex) {
			assertEquals HttpStatus.NOT_ACCEPTABLE, ex.statusCode
		}
	}

	@Test
	void curlReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome('curl/1.2.4', "*/*")
		validateCurlHelpContent(response)
	}

	@Test
	void curlCanStillDownloadZipArchive() {
		ResponseEntity<byte[]> response = execute('/starter.zip', byte[], 'curl/1.2.4', "*/*")
		zipProjectAssert(response.body).isMavenProject().isJavaProject()
	}

	@Test
	void curlCanStillDownloadTgzArchive() {
		ResponseEntity<byte[]> response = execute('/starter.tgz', byte[], 'curl/1.2.4', "*/*")
		tgzProjectAssert(response.body).isMavenProject().isJavaProject()
	}

	@Test
	// make sure curl can still receive metadata with json
	void curlWithAcceptHeaderJson() {
		ResponseEntity<String> response = invokeHome('curl/1.2.4', "application/json")
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE)
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test
	void curlWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome('curl/1.2.4', "text/plain")
		validateCurlHelpContent(response)
	}

	@Test
	void unknownAgentReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome('foo/1.0', "*/*")
		validateCurrentMetadata(response)
	}

	@Test
	void httpieReceivesTextByDefault() {
		ResponseEntity<String> response = invokeHome('HTTPie/0.8.0', "*/*")
		validateHttpIeHelpContent(response)
	}

	@Test
	// make sure curl can still receive metadata with json
	void httpieWithAcceptHeaderJson() {
		ResponseEntity<String> response = invokeHome('HTTPie/0.8.0', "application/json")
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE)
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test
	void httpieWithAcceptHeaderTextPlain() {
		ResponseEntity<String> response = invokeHome('HTTPie/0.8.0', "text/plain")
		validateHttpIeHelpContent(response)
	}

	@Test
	void unknownCliWithTextPlain() {
		ResponseEntity<String> response = invokeHome(null, "text/plain")
		validateGenericHelpContent(response)
	}

	@Test
	void springBootCliReceivesJsonByDefault() {
		ResponseEntity<String> response = invokeHome('SpringBootCli/1.2.0', "*/*")
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE)
		validateCurrentMetadata(new JSONObject(response.body))
	}

	@Test
	void springBootCliWithAcceptHeaderText() {
		ResponseEntity<String> response = invokeHome('SpringBootCli/1.2.0', "text/plain")
		validateSpringBootHelpContent(response)
	}

	@Test // Test that the current output is exactly what we expect
	void validateCurrentProjectMetadata() {
		def json = getMetadataJson()
		validateCurrentMetadata(json)
	}


	private void validateCurlHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN)
		assertThat(response.body, allOf(
				containsString("Spring Initializr"),
				containsString('Examples:'),
				containsString("curl")))
	}

	private void validateHttpIeHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN)
		assertThat(response.body, allOf(
				containsString("Spring Initializr"),
				containsString('Examples:'),
				not(containsString("curl")),
				containsString("http")))
	}

	private void validateGenericHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN)
		assertThat(response.body, allOf(
				containsString("Spring Initializr"),
				not(containsString('Examples:')),
				not(containsString("curl"))))
	}

	private void validateSpringBootHelpContent(ResponseEntity<String> response) {
		validateContentType(response, MediaType.TEXT_PLAIN)
		assertThat(response.body, allOf(
				containsString("Service capabilities"),
				containsString("Supported dependencies"),
				not(containsString('Examples:')),
				not(containsString("curl"))))
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
			downloadArchive('/starter.zip?style=foo:bar')
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

	@Test
	void homeIsForm() {
		def body = htmlHome()
		assertTrue "Wrong body:\n$body", body.contains('action="/starter.zip"')
	}

	@Test
	void homeIsJson() {
		def body = invokeHome(null, null).body
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
	void homeHasOnlyProjectFormatTypes() {
		def body = htmlHome()
		assertTrue 'maven project not found', body.contains('Maven Project')
		assertFalse 'maven pom type should have been filtered', body.contains('Maven POM')
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

	private JSONObject getMetadataJson() {
		getMetadataJson(null, null)
	}

	private JSONObject getMetadataJson(String userAgentHeader, String acceptHeader) {
		String json = invokeHome(userAgentHeader, acceptHeader).body
		return new JSONObject(json)
	}


}
