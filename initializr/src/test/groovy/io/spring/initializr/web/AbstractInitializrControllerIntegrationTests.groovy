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

import io.spring.initializr.DefaultInitializrMetadataProvider
import io.spring.initializr.InitializrMetadata
import io.spring.initializr.InitializrMetadataProvider
import io.spring.initializr.support.ProjectAssert
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.RestTemplate

/**
 * @author Stephane Nicoll
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
@WebAppConfiguration
@IntegrationTest('server.port=0')
abstract class AbstractInitializrControllerIntegrationTests {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder()

	@Value('${local.server.port}')
	private int port

	final RestTemplate restTemplate = new RestTemplate()

	String createUrl(String context) {
		'http://localhost:' + port + context
	}

	String htmlHome() {
		def headers = new HttpHeaders()
		headers.setAccept([MediaType.TEXT_HTML])
		restTemplate.exchange(createUrl('/'), HttpMethod.GET, new HttpEntity<Void>(headers), String).body
	}

	/**
	 * Return a {@link ProjectAssert} for the following archive content.
	 */
	protected ProjectAssert zipProjectAssert(byte[] content) {
		projectAssert(content, ArchiveType.ZIP)
	}

	/**
	 * Return a {@link ProjectAssert} for the following TGZ archive.
	 */
	protected ProjectAssert tgzProjectAssert(byte[] content) {
		projectAssert(content, ArchiveType.TGZ)
	}

	ProjectAssert projectAssert(byte[] content, ArchiveType archiveType) {
		def archiveFile = writeArchive(content)

		def project = folder.newFolder()
		switch (archiveType) {
			case ArchiveType.ZIP:
				new AntBuilder().unzip(dest: project, src: archiveFile)
				break
			case ArchiveType.TGZ:
				new AntBuilder().untar(dest: project, src: archiveFile, compression: 'gzip')
				break
		}
		new ProjectAssert(project)
	}

	protected File writeArchive(byte[] body) {
		def archiveFile = folder.newFile()
		def stream = new FileOutputStream(archiveFile)
		try {
			stream.write(body)
		} finally {
			stream.close()
		}
		archiveFile
	}


	private enum ArchiveType {
		ZIP,

		TGZ
	}

	@EnableAutoConfiguration
	static class Config {

		@Bean
		InitializrMetadataProvider initializrMetadataProvider(InitializrMetadata metadata) {
			new DefaultInitializrMetadataProvider(metadata) {
				@Override
				protected List<InitializrMetadata.BootVersion> fetchBootVersions() {
					null // Disable metadata fetching from spring.io
				}
			}
		}

	}
}
