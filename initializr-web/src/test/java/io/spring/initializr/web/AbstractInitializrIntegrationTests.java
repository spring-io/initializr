/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.initializr.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.test.project.ProjectStructure;
import io.spring.initializr.web.AbstractInitializrIntegrationTests.Config;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
@SpringBootTest(classes = Config.class)
public abstract class AbstractInitializrIntegrationTests {

	protected static final MediaType DEFAULT_METADATA_MEDIA_TYPE = InitializrMetadataVersion.V2_1.getMediaType();

	protected static final MediaType CURRENT_METADATA_MEDIA_TYPE = InitializrMetadataVersion.V2_2.getMediaType();

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public Path folder;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	private RestTemplate restTemplate;

	@BeforeEach
	void before(@TempDir Path folder) {
		this.restTemplate = this.restTemplateBuilder.build();
		this.folder = folder;
	}

	protected abstract String createUrl(String context);

	/**
	 * Validate the "Content-Type" header of the specified response.
	 * @param response the response
	 * @param expected the expected result
	 */
	protected void validateContentType(ResponseEntity<String> response, MediaType expected) {
		MediaType actual = response.getHeaders().getContentType();
		assertThat(actual).isNotNull();
		assertThat(actual.isCompatibleWith(expected))
				.as("Non compatible media-type, expected " + expected + ", got " + actual).isTrue();
	}

	protected JsonNode parseJson(String text) {
		try {
			return objectMapper.readTree(text);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Invalid json", ex);
		}
	}

	protected void validateMetadata(ResponseEntity<String> response, MediaType mediaType, String version,
			JSONCompareMode compareMode) {
		try {
			validateContentType(response, mediaType);
			JSONObject json = new JSONObject(response.getBody());
			JSONObject expected = readMetadataJson(version);
			JSONAssert.assertEquals(expected, json, compareMode);
		}
		catch (JSONException ex) {
			throw new IllegalArgumentException("Invalid json", ex);
		}
	}

	protected void validateDefaultMetadata(ResponseEntity<String> response) {
		validateContentType(response, DEFAULT_METADATA_MEDIA_TYPE);
		validateMetadata(response.getBody(), "2.1.0");
	}

	protected void validateCurrentMetadata(ResponseEntity<String> response) {
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE);
		validateMetadata(response.getBody(), "2.2.0");
	}

	protected void validateDefaultMetadata(String json) {
		validateMetadata(json, "2.1.0");
	}

	protected void validateMetadata(String json, String version) {
		try {
			JSONObject expected = readMetadataJson(version);
			JSONAssert.assertEquals(expected, new JSONObject(json), JSONCompareMode.STRICT);
		}
		catch (JSONException ex) {
			throw new IllegalArgumentException("Invalid json", ex);
		}
	}

	private JSONObject readMetadataJson(String version) {
		return readJsonFrom("metadata/test-default-" + version + ".json");
	}

	/**
	 * Assert that the specified {@link ProjectStructure} has a structure with default
	 * settings, i.e. a Java-based project with the default package and maven build.
	 * @param project the project structure to assert
	 */
	protected void assertDefaultProject(ProjectStructure project) {
		assertDefaultJavaProject(project);
		assertThat(project).containsFiles(".gitignore");
		assertThat(project).hasMavenBuild().hasMavenWrapper().file("mvnw").isExecutable();
	}

	protected void assertDefaultJavaProject(ProjectStructure project) {
		assertThat(project).containsFiles("src/main/java/com/example/demo/DemoApplication.java",
				"src/test/java/com/example/demo/DemoApplicationTests.java",
				"src/main/resources/application.properties");
	}

	protected void assertHasWebResources(ProjectStructure project) {
		assertThat(project).containsDirectories("src/main/resources/templates", "src/main/resources/static");
	}

	protected void assertDoesNotHaveWebResources(ProjectStructure project) {
		assertThat(project).doesNotContainDirectories("src/main/resources/templates", "src/main/resources/static");
	}

	/**
	 * Return a {@link ProjectStructure} for the following archive content.
	 * @param content the source content
	 * @return a project assert
	 */
	protected ProjectStructure projectFromArchive(byte[] content) {
		return getProjectStructure(content, ArchiveType.ZIP);
	}

	/**
	 * Return a {@link ProjectStructure} for the following TGZ archive.
	 * @param content the source content
	 * @return a project assert
	 */
	protected ProjectStructure tgzProjectAssert(byte[] content) {
		return getProjectStructure(content, ArchiveType.TGZ);
	}

	protected ProjectStructure downloadZip(String context) {
		byte[] body = downloadArchive(context).getBody();
		return projectFromArchive(body);
	}

	protected ProjectStructure downloadTgz(String context) {
		byte[] body = downloadArchive(context).getBody();
		return tgzProjectAssert(body);
	}

	protected ResponseEntity<byte[]> downloadArchive(String context) {
		return this.restTemplate.getForEntity(createUrl(context), byte[].class);
	}

	protected ResponseEntity<String> invokeHome(String userAgentHeader, String... acceptHeaders) {
		return execute("/", String.class, userAgentHeader, acceptHeaders);
	}

	protected <T> ResponseEntity<T> execute(String contextPath, Class<T> responseType, String userAgentHeader,
			String... acceptHeaders) {
		HttpHeaders headers = new HttpHeaders();
		if (userAgentHeader != null) {
			headers.set("User-Agent", userAgentHeader);
		}
		if (acceptHeaders != null) {
			List<MediaType> mediaTypes = new ArrayList<>();
			for (String acceptHeader : acceptHeaders) {
				mediaTypes.add(MediaType.parseMediaType(acceptHeader));
			}
			headers.setAccept(mediaTypes);
		}
		else {
			headers.setAccept(Collections.emptyList());
		}
		return this.restTemplate.exchange(createUrl(contextPath), HttpMethod.GET, new HttpEntity<Void>(headers),
				responseType);
	}

	protected ProjectStructure getProjectStructure(byte[] content, ArchiveType archiveType) {
		try {
			Path archiveFile = writeArchive(content);
			Path project = this.folder.resolve("project");
			switch (archiveType) {
			case ZIP:
				unzip(archiveFile, project);
				break;
			case TGZ:
				untar(archiveFile, project);
				break;
			}
			return new ProjectStructure(project);
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot unpack archive", ex);
		}
	}

	private void untar(Path archiveFile, Path project) throws IOException {
		try (TarArchiveInputStream input = new TarArchiveInputStream(
				new GzipCompressorInputStream(Files.newInputStream(archiveFile)))) {
			TarArchiveEntry entry = null;
			while ((entry = input.getNextTarEntry()) != null) {
				Path path = project.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(path);
				}
				else {
					Files.createDirectories(path.getParent());
					Files.write(path, StreamUtils.copyToByteArray(input));
				}
				applyPermissions(path, getPosixFilePermissions(entry.getMode()));
			}
		}
	}

	private void unzip(Path archiveFile, Path project) throws IOException {
		try (ZipFile zip = new ZipFile(archiveFile.toFile())) {
			Enumeration<? extends ZipArchiveEntry> entries = zip.getEntries();
			while (entries.hasMoreElements()) {
				ZipArchiveEntry entry = entries.nextElement();
				Path path = project.resolve(entry.getName());
				if (entry.isDirectory()) {
					Files.createDirectories(path);
				}
				else {
					Files.createDirectories(path.getParent());
					Files.write(path, StreamUtils.copyToByteArray(zip.getInputStream(entry)));
				}
				applyPermissions(path, getPosixFilePermissions(entry.getUnixMode()));
			}
		}
	}

	private Set<PosixFilePermission> getPosixFilePermissions(int unixMode) {
		return Arrays.stream(BitMaskFilePermission.values()).filter((permission) -> permission.permitted(unixMode))
				.map(BitMaskFilePermission::getFilePermission).collect(Collectors.toSet());
	}

	private void applyPermissions(Path target, Set<PosixFilePermission> permissions) throws IOException {
		if (isWindows()) {
			File file = target.toFile();
			applyPermission(file::setReadable, permissions, PosixFilePermission.OWNER_READ,
					PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ);
			applyPermission(file::setWritable, permissions, PosixFilePermission.OWNER_WRITE,
					PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE);
			applyPermission(file::setExecutable, permissions, PosixFilePermission.OWNER_EXECUTE,
					PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE);
		}
		else {
			Files.setPosixFilePermissions(target, permissions);
		}
	}

	private void applyPermission(BiConsumer<Boolean, Boolean> target, Set<PosixFilePermission> permissions,
			PosixFilePermission ownerPermission, PosixFilePermission... nonOwnerPermissions) {
		boolean ownerOnly = Arrays.stream(nonOwnerPermissions).noneMatch(permissions::contains);
		target.accept(permissions.contains(ownerPermission), ownerOnly);
	}

	private boolean isWindows() {
		return File.separatorChar == '\\';
	}

	protected Path writeArchive(byte[] body) throws IOException {
		Path archiveFile = this.folder.resolve("archive");
		Files.write(archiveFile, body);
		return archiveFile;
	}

	protected JSONObject readJsonFrom(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			try (InputStream in = resource.getInputStream()) {
				String json = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
				String placeholder = "";
				if (this instanceof AbstractInitializrControllerIntegrationTests) {
					placeholder = ((AbstractInitializrControllerIntegrationTests) this).host;
				}
				if (this instanceof AbstractFullStackInitializrIntegrationTests) {
					AbstractFullStackInitializrIntegrationTests test = (AbstractFullStackInitializrIntegrationTests) this;
					placeholder = test.host + ":" + test.port;
				}
				// Let's parse the port as it is random
				// TODO: put the port back somehow so it appears in stubs
				String content = json.replaceAll("@host@", placeholder);
				return new JSONObject(content);
			}
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot read JSON from path=" + path);
		}
	}

	public RestTemplate getRestTemplate() {
		return this.restTemplate;
	}

	private enum ArchiveType {

		ZIP,

		TGZ

	}

	@EnableAutoConfiguration
	public static class Config {

	}

	private enum BitMaskFilePermission {

		OWNER_READ(0400),

		OWNER_WRITE(0200),

		OWNER_EXECUTE(0100),

		GROUP_READ(0040),

		GROUP_WRITE(0020),

		GROUP_EXECUTE(0010),

		OTHERS_READ(0004),

		OTHERS_WRITE(0002),

		OTHERS_EXECUTE(0001);

		private int mask;

		private PosixFilePermission filePermission;

		BitMaskFilePermission(int mask) {
			this.mask = mask;
			this.filePermission = PosixFilePermission.valueOf(this.name());
		}

		boolean permitted(int unixMode) {
			return (this.mask & unixMode) == this.mask;
		}

		PosixFilePermission getFilePermission() {
			return this.filePermission;
		}

	}

}
