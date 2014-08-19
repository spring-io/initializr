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

import io.spring.initializr.support.ProjectAssert
import org.junit.Test

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

import static org.junit.Assert.*

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class MainControllerIntegrationTests extends AbstractMainControllerIntegrationTests {

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
		downloadTgz('/starter.tgz?style=data-jpa').isJavaProject().isMavenProject()
				.hasStaticAndTemplatesResources(false).pomAssert()
				.hasDependenciesCount(2)
				.hasSpringBootStarterDependency('data-jpa')
	}

	@Test
	void gradleWarProject() {
		downloadZip('/starter.zip?style=web&style=security&packaging=war&type=gradle.zip').isJavaWarProject()
				.isGradleProject()
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
		assertTrue('Wrong body:\n' + body, body.contains('"version":"1.1.5.RELEASE"'))
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
		File zipFile = writeArchive(body)

		def project = folder.newFolder()
		new AntBuilder().unzip(dest: project, src: zipFile)
		new ProjectAssert(project)
	}

	private ProjectAssert downloadTgz(String context) {
		byte[] body = restTemplate.getForObject(createUrl(context), byte[])
		File tgzFile = writeArchive(body)

		def project = folder.newFolder()
		new AntBuilder().untar(dest: project, src: tgzFile, compression: 'gzip')
		new ProjectAssert(project)
	}

	private File writeArchive(byte[] body) {
		def archiveFile = folder.newFile()
		def stream = new FileOutputStream(archiveFile)
		try {
			stream.write(body)
		} finally {
			stream.close()
		}
		archiveFile
	}

}
