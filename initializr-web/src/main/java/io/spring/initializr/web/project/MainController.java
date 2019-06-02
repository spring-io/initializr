/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.web.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.generator.project.ResolvedProjectDescription;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.mapper.DependencyMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataJsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV2JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import io.spring.initializr.web.support.Agent;
import io.spring.initializr.web.support.Agent.AgentId;
import io.spring.initializr.web.support.CommandLineHelpGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Tar;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.TarFileSet;
import org.apache.tools.ant.types.ZipFileSet;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The main initializr controller provides access to the configured metadata and serves as
 * a central endpoint to generate projects or build files.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
@Controller
public class MainController extends AbstractInitializrController {

	private static final Log logger = LogFactory.getLog(MainController.class);

	/**
	 * HAL JSON content type.
	 */
	public static final MediaType HAL_JSON_CONTENT_TYPE = MediaType
			.parseMediaType("application/hal+json");

	private final DependencyMetadataProvider dependencyMetadataProvider;

	private final CommandLineHelpGenerator commandLineHelpGenerator;

	private final ProjectGenerationInvoker projectGenerationInvoker;

	public MainController(InitializrMetadataProvider metadataProvider,
			TemplateRenderer templateRenderer,
			DependencyMetadataProvider dependencyMetadataProvider,
			ProjectGenerationInvoker projectGenerationInvoker) {
		super(metadataProvider);
		this.dependencyMetadataProvider = dependencyMetadataProvider;
		this.commandLineHelpGenerator = new CommandLineHelpGenerator(templateRenderer);
		this.projectGenerationInvoker = projectGenerationInvoker;
	}

	@ModelAttribute
	public ProjectRequest projectRequest(@RequestHeader Map<String, String> headers) {
		WebProjectRequest request = new WebProjectRequest();
		request.getParameters().putAll(headers);
		request.initialize(this.metadataProvider.get());
		return request;
	}

	@RequestMapping(path = "/metadata/config", produces = "application/json")
	@ResponseBody
	public InitializrMetadata config() {
		return this.metadataProvider.get();
	}

	@RequestMapping(path = "/", produces = "text/plain")
	public ResponseEntity<String> serviceCapabilitiesText(
			@RequestHeader(value = HttpHeaders.USER_AGENT,
					required = false) String userAgent)
			throws IOException {
		String appUrl = generateAppUrl();
		InitializrMetadata metadata = this.metadataProvider.get();

		BodyBuilder builder = ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN);
		if (userAgent != null) {
			Agent agent = Agent.fromUserAgent(userAgent);
			if (agent != null) {
				if (AgentId.CURL.equals(agent.getId())) {
					String content = this.commandLineHelpGenerator
							.generateCurlCapabilities(metadata, appUrl);
					return builder.eTag(createUniqueId(content)).body(content);
				}
				if (AgentId.HTTPIE.equals(agent.getId())) {
					String content = this.commandLineHelpGenerator
							.generateHttpieCapabilities(metadata, appUrl);
					return builder.eTag(createUniqueId(content)).body(content);
				}
				if (AgentId.SPRING_BOOT_CLI.equals(agent.getId())) {
					String content = this.commandLineHelpGenerator
							.generateSpringBootCliCapabilities(metadata, appUrl);
					return builder.eTag(createUniqueId(content)).body(content);
				}
			}
		}
		String content = this.commandLineHelpGenerator
				.generateGenericCapabilities(metadata, appUrl);
		return builder.eTag(createUniqueId(content)).body(content);
	}

	@RequestMapping(path = { "/", "/metadata/client" }, produces = "application/hal+json")
	public ResponseEntity<String> serviceCapabilitiesHal() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1,
				HAL_JSON_CONTENT_TYPE);
	}

	@RequestMapping(path = { "/", "/metadata/client" },
			produces = { "application/vnd.initializr.v2.1+json", "application/json" })
	public ResponseEntity<String> serviceCapabilitiesV21() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1);
	}

	@RequestMapping(path = { "/", "/metadata/client" },
			produces = "application/vnd.initializr.v2+json")
	public ResponseEntity<String> serviceCapabilitiesV2() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2);
	}

	private ResponseEntity<String> serviceCapabilitiesFor(
			InitializrMetadataVersion version) {
		return serviceCapabilitiesFor(version, version.getMediaType());
	}

	private ResponseEntity<String> serviceCapabilitiesFor(
			InitializrMetadataVersion version, MediaType contentType) {
		String appUrl = generateAppUrl();
		String content = getJsonMapper(version).write(this.metadataProvider.get(),
				appUrl);
		return ResponseEntity.ok().contentType(contentType).eTag(createUniqueId(content))
				.varyBy("Accept").cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
				.body(content);
	}

	private static InitializrMetadataJsonMapper getJsonMapper(
			InitializrMetadataVersion version) {
		switch (version) {
		case V2:
			return new InitializrMetadataV2JsonMapper();
		default:
			return new InitializrMetadataV21JsonMapper();
		}
	}

	@RequestMapping(path = "/dependencies",
			produces = { "application/vnd.initializr.v2.1+json", "application/json" })
	public ResponseEntity<String> dependenciesV21(
			@RequestParam(required = false) String bootVersion) {
		return dependenciesFor(InitializrMetadataVersion.V2_1, bootVersion);
	}

	private ResponseEntity<String> dependenciesFor(InitializrMetadataVersion version,
			String bootVersion) {
		InitializrMetadata metadata = this.metadataProvider.get();
		Version v = (bootVersion != null) ? Version.parse(bootVersion)
				: Version.parse(metadata.getBootVersions().getDefault().getId());
		DependencyMetadata dependencyMetadata = this.dependencyMetadataProvider
				.get(metadata, v);
		String content = new DependencyMetadataV21JsonMapper().write(dependencyMetadata);
		return ResponseEntity.ok().contentType(version.getMediaType())
				.eTag(createUniqueId(content))
				.cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS)).body(content);
	}

	@RequestMapping(path = { "/spring", "/spring.zip" })
	public String spring() {
		String url = this.metadataProvider.get().createCliDistributionURl("zip");
		return "redirect:" + url;
	}

	@RequestMapping(path = { "/spring.tar.gz", "spring.tgz" })
	public String springTgz() {
		String url = this.metadataProvider.get().createCliDistributionURl("tar.gz");
		return "redirect:" + url;
	}

	@RequestMapping(path = { "/pom", "/pom.xml" })
	@ResponseBody
	public ResponseEntity<byte[]> pom(ProjectRequest request) {
		request.setType("maven-build");
		byte[] mavenPom = this.projectGenerationInvoker.invokeBuildGeneration(request);
		return createResponseEntity(mavenPom, "application/octet-stream", "pom.xml");
	}

	@RequestMapping(path = { "/build", "/build.gradle" })
	@ResponseBody
	public ResponseEntity<byte[]> gradle(ProjectRequest request) {
		request.setType("gradle-build");
		byte[] gradleBuild = this.projectGenerationInvoker.invokeBuildGeneration(request);
		return createResponseEntity(gradleBuild, "application/octet-stream",
				"build.gradle");
	}

	@RequestMapping("/starter.zip")
	@ResponseBody
	public ResponseEntity<byte[]> springZip(ProjectRequest request) throws IOException {
		ProjectGenerationResult result = this.projectGenerationInvoker
				.invokeProjectStructureGeneration(request);
		File dir = result.getRootDirectory().toFile();
		File download = this.projectGenerationInvoker.createDistributionFile(dir, ".zip");
		String wrapperScript = getWrapperScript(result.getProjectDescription());
		Zip zip = new Zip();
		zip.setProject(new Project());
		zip.setDefaultexcludes(false);
		ZipFileSet set = new ZipFileSet();
		set.setDir(dir);
		set.setFileMode("755");
		set.setIncludes(wrapperScript);
		set.setDefaultexcludes(false);
		zip.addFileset(set);
		set = new ZipFileSet();
		set.setDir(dir);
		set.setIncludes("**,");
		set.setExcludes(wrapperScript);
		set.setDefaultexcludes(false);
		zip.addFileset(set);
		zip.setDestFile(download.getCanonicalFile());
		zip.execute();
		return upload(download, dir, generateFileName(request, "zip"), "application/zip");
	}

	@RequestMapping(path = "/starter.tgz", produces = "application/x-compress")
	@ResponseBody
	public ResponseEntity<byte[]> springTgz(ProjectRequest request) throws IOException {
		ProjectGenerationResult result = this.projectGenerationInvoker
				.invokeProjectStructureGeneration(request);
		File dir = result.getRootDirectory().toFile();
		File download = this.projectGenerationInvoker.createDistributionFile(dir,
				".tar.gz");
		String wrapperScript = getWrapperScript(result.getProjectDescription());
		Tar zip = new Tar();
		zip.setProject(new Project());
		zip.setDefaultexcludes(false);
		TarFileSet set = zip.createTarFileSet();
		set.setDir(dir);
		set.setFileMode("755");
		set.setIncludes(wrapperScript);
		set.setDefaultexcludes(false);
		set = zip.createTarFileSet();
		set.setDir(dir);
		set.setIncludes("**,");
		set.setExcludes(wrapperScript);
		set.setDefaultexcludes(false);
		zip.setDestFile(download.getCanonicalFile());
		Tar.TarCompressionMethod method = new Tar.TarCompressionMethod();
		method.setValue("gzip");
		zip.setCompression(method);
		zip.execute();
		return upload(download, dir, generateFileName(request, "tar.gz"),
				"application/x-compress");
	}

	private String generateFileName(ProjectRequest request, String extension) {
		String candidate = (StringUtils.hasText(request.getArtifactId())
				? request.getArtifactId()
				: this.metadataProvider.get().getArtifactId().getContent());
		String tmp = candidate.replaceAll(" ", "_");
		try {
			return URLEncoder.encode(tmp, "UTF-8") + "." + extension;
		}
		catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("Cannot encode URL", ex);
		}
	}

	private static String getWrapperScript(ResolvedProjectDescription description) {
		BuildSystem buildSystem = description.getBuildSystem();
		String script = buildSystem.id().equals(MavenBuildSystem.ID) ? "mvnw" : "gradlew";
		return (description.getBaseDirectory() != null)
				? description.getBaseDirectory() + "/" + script : script;
	}

	private ResponseEntity<byte[]> upload(File download, File dir, String fileName,
			String contentType) throws IOException {
		byte[] bytes = StreamUtils.copyToByteArray(new FileInputStream(download));
		logger.info(String.format("Uploading: %s (%s bytes)", download, bytes.length));
		ResponseEntity<byte[]> result = createResponseEntity(bytes, contentType,
				fileName);
		this.projectGenerationInvoker.cleanTempFiles(dir);
		return result;
	}

	private ResponseEntity<byte[]> createResponseEntity(byte[] content,
			String contentType, String fileName) {
		String contentDispositionValue = "attachment; filename=\"" + fileName + "\"";
		return ResponseEntity.ok().header("Content-Type", contentType)
				.header("Content-Disposition", contentDispositionValue).body(content);
	}

	private String createUniqueId(String content) {
		StringBuilder builder = new StringBuilder();
		DigestUtils.appendMd5DigestAsHex(content.getBytes(StandardCharsets.UTF_8),
				builder);
		return builder.toString();
	}

}
