/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for Initializr integration tests. Provides assertions on metadata and
 * generated projects, but no way to invoke the service. Extend
 * {@link AbstractInitializrControllerIntegrationTests} or
 * {@link AbstractFullStackInitializrIntegrationTests} instead.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
@SpringBootTest(classes = Config.class)
public abstract class AbstractInitializrIntegrationTests {

	protected static final MediaType DEFAULT_METADATA_MEDIA_TYPE = InitializrMetadataVersion.V2_1.getMediaType();

	protected static final MediaType CURRENT_METADATA_MEDIA_TYPE = InitializrMetadataVersion.V2_3.getMediaType();

	private static final JsonMapper jsonMapper = JsonMapper.builder().build();

	private Path folder;

	@BeforeEach
	void before(@TempDir Path folder) {
		this.folder = folder;
	}

	/**
	 * Return the authority ({@code host[:port]}) of the service under test. Replaces the
	 * {@code @host@} placeholder in JSON files read by {@link #readJsonFrom(String)}.
	 * @return the authority, e.g. {@code start.example.com} or {@code localhost:8080}
	 */
	protected String authority() {
		return "";
	}

	protected JsonNode parseJson(String text) {
		return jsonMapper.readTree(text);
	}

	protected void validateDefaultMetadata(@Nullable String json) {
		validateMetadata(json, "2.1.0");
	}

	protected void validateMetadata(@Nullable String json, String version) {
		validateMetadata(json, version, JSONCompareMode.STRICT);
	}

	protected void validateMetadata(@Nullable String json, String version, JSONCompareMode compareMode) {
		try {
			JSONObject expected = readMetadataJson(version);
			JSONAssert.assertEquals(expected, new JSONObject(json), compareMode);
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
				"src/test/java/com/example/demo/DemoApplicationTests.java");
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

	protected ProjectStructure getProjectStructure(byte[] content, ArchiveType archiveType) {
		try {
			Path archiveFile = writeArchive(content);
			Path project = this.folder.resolve("project");
			switch (archiveType) {
				case ZIP -> unzip(archiveFile, project);
				case TGZ -> untar(archiveFile, project);
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
			TarArchiveEntry entry;
			while ((entry = input.getNextEntry()) != null) {
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
		try (ZipFile zip = ZipFile.builder().setPath(archiveFile).get()) {
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
		return Arrays.stream(BitMaskFilePermission.values())
			.filter((permission) -> permission.permitted(unixMode))
			.map(BitMaskFilePermission::getFilePermission)
			.collect(Collectors.toSet());
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
				String json = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
				String content = json.replaceAll("@host@", authority());
				return new JSONObject(content);
			}
		}
		catch (Exception ex) {
			throw new IllegalStateException("Cannot read JSON from path=" + path);
		}
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

		private final int mask;

		private final PosixFilePermission filePermission;

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
