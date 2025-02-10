/*
 * Copyright 2012-2023 the original author or authors.
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

package io.spring.initializr.web.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrConfiguration.Platform;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InvalidInitializrMetadataException;
import io.spring.initializr.web.mapper.DependencyMetadataJsonMapper;
import io.spring.initializr.web.mapper.DependencyMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataJsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV22JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV2JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import io.spring.initializr.web.project.InvalidProjectRequestException;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link RestController} that exposes metadata and service configuration.
 *
 * @author Stephane Nicoll
 * @author Moritz Halbritter
 */
@RestController
public class ProjectMetadataController extends AbstractMetadataController {

	/**
	 * HAL JSON content type.
	 */
	public static final MediaType HAL_JSON_CONTENT_TYPE = MediaType.parseMediaType("application/hal+json");

	private final DependencyMetadataProvider dependencyMetadataProvider;

	public ProjectMetadataController(InitializrMetadataProvider metadataProvider,
			DependencyMetadataProvider dependencyMetadataProvider) {
		super(metadataProvider);
		this.dependencyMetadataProvider = dependencyMetadataProvider;
	}

	@GetMapping(path = "/metadata/config", produces = "application/json")
	public InitializrMetadata config() {
		return this.metadataProvider.get();
	}

	@GetMapping(path = { "/", "/metadata/client" }, produces = "application/hal+json")
	public ResponseEntity<String> serviceCapabilitiesHal() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1, HAL_JSON_CONTENT_TYPE);
	}

	@GetMapping(path = { "/", "/metadata/client" }, produces = { "application/vnd.initializr.v2.2+json" })
	public ResponseEntity<String> serviceCapabilitiesV22() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_2);
	}

	@GetMapping(path = { "/", "/metadata/client" },
			produces = { "application/vnd.initializr.v2.1+json", "application/json" })
	public ResponseEntity<String> serviceCapabilitiesV21() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1);
	}

	@GetMapping(path = { "/", "/metadata/client" }, produces = "application/vnd.initializr.v2+json")
	public ResponseEntity<String> serviceCapabilitiesV2() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2);
	}

	@GetMapping(path = "/dependencies", produces = "application/vnd.initializr.v2.2+json")
	public ResponseEntity<String> dependenciesV22(@RequestParam(required = false) String bootVersion) {
		return dependenciesFor(InitializrMetadataVersion.V2_2, bootVersion);
	}

	@GetMapping(path = "/dependencies", produces = { "application/vnd.initializr.v2.1+json", "application/json" })
	public ResponseEntity<String> dependenciesV21(@RequestParam(required = false) String bootVersion) {
		return dependenciesFor(InitializrMetadataVersion.V2_1, bootVersion);
	}

	@ExceptionHandler
	public void invalidMetadataRequest(HttpServletResponse response, InvalidInitializrMetadataException ex)
			throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	@ExceptionHandler
	public void invalidProjectRequest(HttpServletResponse response, InvalidProjectRequestException ex)
			throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	}

	/**
	 * Return the {@link CacheControl} response headers to use for the specified
	 * {@link InitializrMetadata metadata}. If no cache should be applied
	 * {@link CacheControl#empty()} can be used.
	 * @param metadata the metadata about to be exposed
	 * @return the {@code Cache-Control} headers to use
	 */
	protected CacheControl determineCacheControlFor(InitializrMetadata metadata) {
		return CacheControl.maxAge(2, TimeUnit.HOURS);
	}

	/**
	 * Create the dependencies {@link ResponseEntity} for the given version and Spring
	 * Boot version.
	 * @param metadataVersion the metadata version
	 * @param bootVersion the Spring Boot version.
	 * @return the {@link ResponseEntity}
	 */
	protected ResponseEntity<String> dependenciesFor(InitializrMetadataVersion metadataVersion, String bootVersion) {
		InitializrMetadata metadata = this.metadataProvider.get();
		Version effectiveBootVersion = (bootVersion != null) ? Version.parse(bootVersion)
				: Version.parse(metadata.getBootVersions().getDefault().getId());
		Platform platform = metadata.getConfiguration().getEnv().getPlatform();
		if (!platform.isCompatibleVersion(effectiveBootVersion)) {
			throw new InvalidProjectRequestException("Invalid Spring Boot version '" + bootVersion
					+ "', Spring Boot compatibility range is " + platform.determineCompatibilityRangeRequirement());
		}
		DependencyMetadata dependencyMetadata = this.dependencyMetadataProvider.get(metadata, effectiveBootVersion);
		String content = createDependencyJsonMapper(metadataVersion).write(dependencyMetadata);
		return ResponseEntity.ok()
			.contentType(metadataVersion.getMediaType())
			.eTag(createUniqueId(content))
			.cacheControl(determineCacheControlFor(metadata))
			.body(content);
	}

	private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion metadataVersion) {
		return serviceCapabilitiesFor(metadataVersion, metadataVersion.getMediaType());
	}

	/**
	 * Create the service capabilities {@link ResponseEntity} for the given metadata
	 * version and content type.
	 * @param metadataVersion the metadata version
	 * @param contentType the content type
	 * @return the {@link ResponseEntity}
	 */
	protected ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion metadataVersion,
			MediaType contentType) {
		String appUrl = generateAppUrl();
		InitializrMetadata metadata = this.metadataProvider.get();
		String content = createMetadataJsonMapper(metadataVersion).write(metadata, appUrl);
		return ResponseEntity.ok()
			.contentType(contentType)
			.eTag(createUniqueId(content))
			.varyBy("Accept")
			.cacheControl(determineCacheControlFor(metadata))
			.body(content);
	}

	/**
	 * Create the {@link InitializrMetadataJsonMapper JSON mapper} for the given metadata
	 * version.
	 * @param metadataVersion the metadata version
	 * @return the JSON mapper
	 */
	protected InitializrMetadataJsonMapper createMetadataJsonMapper(InitializrMetadataVersion metadataVersion) {
		return switch (metadataVersion) {
			case V2 -> new InitializrMetadataV2JsonMapper();
			case V2_1 -> new InitializrMetadataV21JsonMapper();
			default -> new InitializrMetadataV22JsonMapper();
		};
	}

	/**
	 * Create the {@link DependencyMetadataJsonMapper JSON mapper} for the given metadata
	 * version.
	 * @param metadataVersion the metadata version
	 * @return the JSON mapper
	 */
	protected DependencyMetadataJsonMapper createDependencyJsonMapper(InitializrMetadataVersion metadataVersion) {
		return new DependencyMetadataV21JsonMapper();
	}

}
