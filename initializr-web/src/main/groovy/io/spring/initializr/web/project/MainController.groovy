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

package io.spring.initializr.web.project

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

import groovy.util.logging.Slf4j
import io.spring.initializr.generator.BasicProjectRequest
import io.spring.initializr.generator.CommandLineHelpGenerator
import io.spring.initializr.generator.ProjectGenerator
import io.spring.initializr.generator.ProjectRequest
import io.spring.initializr.util.Agent
import io.spring.initializr.web.mapper.DependencyMetadataV21JsonMapper
import io.spring.initializr.web.mapper.InitializrMetadataJsonMapper
import io.spring.initializr.web.mapper.InitializrMetadataV21JsonMapper
import io.spring.initializr.web.mapper.InitializrMetadataV2JsonMapper
import io.spring.initializr.web.mapper.InitializrMetadataVersion
import io.spring.initializr.metadata.DependencyMetadataProvider
import io.spring.initializr.metadata.InitializrMetadata
import io.spring.initializr.util.Version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.DigestUtils
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import static io.spring.initializr.util.Agent.AgentId.CURL
import static io.spring.initializr.util.Agent.AgentId.HTTPIE
import static io.spring.initializr.util.Agent.AgentId.SPRING_BOOT_CLI

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

	static final MediaType HAL_JSON_CONTENT_TYPE = MediaType.parseMediaType('application/hal+json')

	@Autowired
	private ProjectGenerator projectGenerator

	@Autowired
	private DependencyMetadataProvider dependencyMetadataProvider

	private CommandLineHelpGenerator commandLineHelpGenerator = new CommandLineHelpGenerator()


	@ModelAttribute
	BasicProjectRequest projectRequest(@RequestHeader Map<String,String> headers) {
		def request = new ProjectRequest()
		request.parameters << headers
		request.initialize(metadataProvider.get())
		request
	}

	@RequestMapping(value = "/metadata/config", produces = ["application/json"])
	@ResponseBody
	InitializrMetadata config() {
		metadataProvider.get()
	}

	@RequestMapping(value = "/metadata/client")
	String client() {
		'redirect:/'
	}


	@RequestMapping(value = "/", produces = ["text/plain"])
	ResponseEntity<String> serviceCapabilitiesText(
			@RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent) {
		String appUrl = generateAppUrl()
		def metadata = metadataProvider.get()

		def builder = ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN)
		if (userAgent) {
			Agent agent = Agent.fromUserAgent(userAgent)
			if (CURL.equals(agent?.id)) {
				def content = commandLineHelpGenerator.generateCurlCapabilities(metadata, appUrl)
				return builder.eTag(createUniqueId(content)).body(content)
			}
			if (HTTPIE.equals(agent?.id)) {
				def content = commandLineHelpGenerator.generateHttpieCapabilities(metadata, appUrl)
				return builder.eTag(createUniqueId(content)).body(content)
			}
			if (SPRING_BOOT_CLI.equals(agent?.id)) {
				def content = commandLineHelpGenerator.generateSpringBootCliCapabilities(metadata, appUrl)
				return builder.eTag(createUniqueId(content)).body(content)
			}
		}
		def content = commandLineHelpGenerator.generateGenericCapabilities(metadata, appUrl)
		builder.eTag(createUniqueId(content)).body(content)
	}

	@RequestMapping(value = "/", produces = ["application/hal+json"])
	ResponseEntity<String> serviceCapabilitiesHal() {
		serviceCapabilitiesFor(InitializrMetadataVersion.V2_1, HAL_JSON_CONTENT_TYPE)
	}

	@RequestMapping(value = "/", produces = ["application/vnd.initializr.v2.1+json", "application/json"])
	ResponseEntity<String> serviceCapabilitiesV21() {
		serviceCapabilitiesFor(InitializrMetadataVersion.V2_1)
	}

	@RequestMapping(value = "/", produces = ["application/vnd.initializr.v2+json"])
	ResponseEntity<String> serviceCapabilitiesV2() {
		serviceCapabilitiesFor(InitializrMetadataVersion.V2)
	}

	private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion version) {
		serviceCapabilitiesFor(version, version.mediaType)
	}

	private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion version, MediaType contentType) {
		String appUrl = generateAppUrl()
		def content = getJsonMapper(version).write(metadataProvider.get(), appUrl)
		return ResponseEntity.ok().contentType(contentType).eTag(createUniqueId(content))
				.cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)).body(content)
	}

	private static InitializrMetadataJsonMapper getJsonMapper(InitializrMetadataVersion version) {
		switch (version) {
			case InitializrMetadataVersion.V2: return new InitializrMetadataV2JsonMapper();
			default: return new InitializrMetadataV21JsonMapper();
		}
	}

	@RequestMapping(value = "/dependencies", produces = ["application/vnd.initializr.v2.1+json", "application/json"])
	ResponseEntity<String> dependenciesV21(@RequestParam(required = false) String bootVersion) {
		dependenciesFor(InitializrMetadataVersion.V2_1, bootVersion)
	}

	private ResponseEntity<String> dependenciesFor(InitializrMetadataVersion version, String bootVersion) {
		def metadata = metadataProvider.get()
		Version v = bootVersion != null ? Version.parse(bootVersion) :
				Version.parse(metadata.bootVersions.getDefault().id);
		def dependencyMetadata = dependencyMetadataProvider.get(metadata, v)
		def content = new DependencyMetadataV21JsonMapper().write(dependencyMetadata)
		return ResponseEntity.ok().contentType(version.mediaType).eTag(createUniqueId(content))
				.cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)).body(content)
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
	ResponseEntity<byte[]> pom(BasicProjectRequest request) {
		def mavenPom = projectGenerator.generateMavenPom((ProjectRequest) request)
		createResponseEntity(mavenPom, 'application/octet-stream', 'pom.xml')
	}

	@RequestMapping('/build')
	@ResponseBody
	ResponseEntity<byte[]> gradle(BasicProjectRequest request) {
		def gradleBuild = projectGenerator.generateGradleBuild((ProjectRequest) request)
		createResponseEntity(gradleBuild, 'application/octet-stream', 'build.gradle')
	}

	@RequestMapping('/starter.zip')
	@ResponseBody
	ResponseEntity<byte[]> springZip(BasicProjectRequest basicRequest) {
		ProjectRequest request = (ProjectRequest) basicRequest
		def dir = projectGenerator.generateProjectStructure(request)

		def download = projectGenerator.createDistributionFile(dir, '.zip')

		def wrapperScript = getWrapperScript(request)

		new AntBuilder().zip(destfile: download) {
			zipfileset(dir: dir, includes: wrapperScript, filemode: 755)
			zipfileset(dir: dir, includes: '**', excludes: wrapperScript)
		}
		upload(download, dir, generateFileName(request, 'zip'), 'application/zip')
	}

	@RequestMapping(value = '/starter.tgz', produces = 'application/x-compress')
	@ResponseBody
	ResponseEntity<byte[]> springTgz(BasicProjectRequest basicRequest) {
		ProjectRequest request = (ProjectRequest) basicRequest
		def dir = projectGenerator.generateProjectStructure(request)

		def download = projectGenerator.createDistributionFile(dir, '.tgz')

		def wrapperScript = getWrapperScript(request)

		new AntBuilder().tar(destfile: download, compression: 'gzip') {
			zipfileset(dir: dir, includes: wrapperScript, filemode: 755)
			zipfileset(dir: dir, includes: '**', excludes: wrapperScript)
		}
		upload(download, dir, generateFileName(request, 'tgz'), 'application/x-compress')
	}

	private static String generateFileName(ProjectRequest request, String extension) {
		String tmp = request.artifactId.replaceAll(' ', '_')
		URLEncoder.encode(tmp, 'UTF-8') + '.' + extension
	}

	private static String getWrapperScript(ProjectRequest request) {
		def script = 'gradle'.equals(request.build) ? 'gradlew' : 'mvnw'
		def wrapperScript = request.baseDir ? "$request.baseDir/$script" : script
		wrapperScript
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

	private String createUniqueId(String content) {
		StringBuilder builder = new StringBuilder()
		DigestUtils.appendMd5DigestAsHex(content.getBytes(StandardCharsets.UTF_8), builder)
		builder.toString()
	}

}
