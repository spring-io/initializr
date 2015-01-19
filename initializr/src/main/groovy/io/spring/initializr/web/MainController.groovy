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

import groovy.util.logging.Slf4j
import io.spring.initializr.CommandLineHelpGenerator
import io.spring.initializr.InitializrMetadata
import io.spring.initializr.ProjectGenerator
import io.spring.initializr.ProjectRequest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

/**
 * The main initializr controller provides access to the configured
 * metadata and serves as a central endpoint to generate projects
 * or build files.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @since 1.0
 */
@Controller
@Slf4j
class MainController extends AbstractInitializrController {

	public static final MediaType META_DATA_V2 = MediaType.parseMediaType("application/vnd.initializr.v2+json")

	@Autowired
	private ProjectGenerator projectGenerator

	private CommandLineHelpGenerator commandLineHelpGenerator = new CommandLineHelpGenerator()


	@ModelAttribute
	ProjectRequest projectRequest() {
		def request = new ProjectRequest()
		metadataProvider.get().initializeProjectRequest(request)
		request
	}

	@RequestMapping(value = "/", headers = "user-agent=SpringBootCli/1.2.0.RC1")
	@ResponseBody
	@Deprecated
	InitializrMetadata oldMetadata() {
		metadataProvider.get()
	}

	@RequestMapping(value = "/", produces = ["text/plain"])
	ResponseEntity<String> serviceCapabilities(
			@RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent) {
		String appUrl = ServletUriComponentsBuilder.fromCurrentServletMapping().build()
		def metadata = metadataProvider.get()

		def builder = ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
		if (userAgent && userAgent.startsWith(WebConfig.CURL_USER_AGENT_PREFIX)) {
			builder.body(commandLineHelpGenerator.generateCurlCapabilities(metadata,appUrl))
		}
		else {
			builder.body(commandLineHelpGenerator.generateGenericCapabilities(metadata, appUrl))
		}
	}

	@RequestMapping(value = "/", produces = ["application/vnd.initializr.v2+json", "application/json"])
	ResponseEntity<String> serviceCapabilities() {
		String appUrl = ServletUriComponentsBuilder.fromCurrentServletMapping().build()
		def content = metadataProvider.get().generateJson(appUrl)
		return ResponseEntity.ok().contentType(META_DATA_V2).body(content)
	}

	@RequestMapping(value = '/', produces = 'text/html')
	@ResponseBody
	String home() {
		renderHome('home.html')
	}

	@RequestMapping('/spring')
	String spring() {
		def url = metadataProvider.get().createCliDistributionURl('zip')
		"redirect:$url"
	}

	@RequestMapping(value = ['/spring.tar.gz', 'spring.tgz'])
	String springTgz() {
		def url = metadataProvider.get().createCliDistributionURl('tar.gz')
		"redirect:$url"
	}

	@RequestMapping('/pom')
	@ResponseBody
	ResponseEntity<byte[]> pom(ProjectRequest request) {
		def mavenPom = projectGenerator.generateMavenPom(request)
		createResponseEntity(mavenPom, 'application/octet-stream', 'pom.xml')
	}

	@RequestMapping('/build')
	@ResponseBody
	ResponseEntity<byte[]> gradle(ProjectRequest request) {
		def gradleBuild = projectGenerator.generateGradleBuild(request)
		createResponseEntity(gradleBuild, 'application/octet-stream', 'build.gradle')
	}

	@RequestMapping('/starter.zip')
	@ResponseBody
	ResponseEntity<byte[]> springZip(ProjectRequest request) {
		def dir = projectGenerator.generateProjectStructure(request)

		def download = projectGenerator.createDistributionFile(dir, '.zip')

		new AntBuilder().zip(destfile: download) {
			zipfileset(dir: dir, includes: '**')
		}
		upload(download, dir, generateFileName(request, 'zip'), 'application/zip')
	}

	@RequestMapping(value = '/starter.tgz', produces = 'application/x-compress')
	@ResponseBody
	ResponseEntity<byte[]> springTgz(ProjectRequest request) {
		def dir = projectGenerator.generateProjectStructure(request)

		def download = projectGenerator.createDistributionFile(dir, '.tgz')

		new AntBuilder().tar(destfile: download, compression: 'gzip') {
			zipfileset(dir: dir, includes: '**')
		}
		upload(download, dir, generateFileName(request, 'tgz'), 'application/x-compress')
	}

	private static String generateFileName(ProjectRequest request, String extension) {
		String tmp = request.artifactId.replaceAll(' ', '_')
		URLEncoder.encode(tmp, 'UTF-8') + '.' + extension
	}

	private ResponseEntity<byte[]> upload(File download, File dir, String fileName, String contentType) {
		log.info("Uploading: ${download} (${download.bytes.length} bytes)")
		ResponseEntity<byte[]> result = createResponseEntity(download.bytes, contentType, fileName)
		projectGenerator.cleanTempFiles(dir)
		result
	}

	private ResponseEntity<byte[]> createResponseEntity(byte[] content, String contentType, String fileName) {
		String contentDispositionValue = "attachment; filename=\"$fileName\""
		def result = new ResponseEntity<byte[]>(content,
				['Content-Type'       : contentType,
				 'Content-Disposition': contentDispositionValue] as HttpHeaders, HttpStatus.OK)
		result
	}

}
