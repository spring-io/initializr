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

package io.spring.initializr.web.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrConfiguration.Platform;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InvalidInitializrMetadataException;
import io.spring.initializr.web.mapper.DependencyMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataJsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV22JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataV2JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import io.spring.initializr.web.project.InvalidProjectRequestException;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * {@link Controller} that exposes metadata and service configuration.
 *
 * @author Stephane Nicoll
 */
@Controller
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

	@RequestMapping(path = "/metadata/config", produces = "application/json")
	@ResponseBody
	public InitializrMetadata config() {
		return this.metadataProvider.get();
	}

	@RequestMapping(path = { "/", "/metadata/client" }, produces = "application/hal+json")
	public ResponseEntity<String> serviceCapabilitiesHal() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1, HAL_JSON_CONTENT_TYPE);
	}

	@RequestMapping(path = { "/", "/metadata/client" }, produces = { "application/vnd.initializr.v2.2+json" })
	public ResponseEntity<String> serviceCapabilitiesV22() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_2);
	}

	@RequestMapping(path = { "/", "/metadata/client" },
			produces = { "application/vnd.initializr.v2.1+json", "application/json" })
	public ResponseEntity<String> serviceCapabilitiesV21() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1);
	}

	@RequestMapping(path = { "/", "/metadata/client" }, produces = "application/vnd.initializr.v2+json")
	public ResponseEntity<String> serviceCapabilitiesV2() {
		return serviceCapabilitiesFor(InitializrMetadataVersion.V2);
	}

	@RequestMapping(path = "/dependencies", produces = { "application/vnd.initializr.v2.1+json", "application/json" })
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

	private ResponseEntity<String> dependenciesFor(InitializrMetadataVersion version, String bootVersion) {
		InitializrMetadata metadata = this.metadataProvider.get();
		Version v = (bootVersion != null) ? Version.parse(bootVersion)
				: Version.parse(metadata.getBootVersions().getDefault().getId());
		Platform platform = metadata.getConfiguration().getEnv().getPlatform();
		if (!platform.isCompatibleVersion(v)) {
			throw new InvalidProjectRequestException("Invalid Spring Boot version '" + bootVersion
					+ "', Spring Boot compatibility range is " + platform.determineCompatibilityRangeRequirement());
		}
		DependencyMetadata dependencyMetadata = this.dependencyMetadataProvider.get(metadata, v);
		String content = new DependencyMetadataV21JsonMapper().write(dependencyMetadata);
		return ResponseEntity.ok().contentType(version.getMediaType()).eTag(createUniqueId(content))
				.cacheControl(determineCacheControlFor(metadata)).body(content);
	}

	private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion version) {
		return serviceCapabilitiesFor(version, version.getMediaType());
	}

	private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion version, MediaType contentType) {
		String appUrl = generateAppUrl();
		InitializrMetadata metadata = this.metadataProvider.get();
		String content = getJsonMapper(version).write(metadata, appUrl);
		return ResponseEntity.ok().contentType(contentType).eTag(createUniqueId(content)).varyBy("Accept")
				.cacheControl(determineCacheControlFor(metadata)).body(content);
	}

	private static InitializrMetadataJsonMapper getJsonMapper(InitializrMetadataVersion version) {
		switch (version) {
		case V2:
			return new InitializrMetadataV2JsonMapper();
		case V2_1:
			return new InitializrMetadataV21JsonMapper();
		default:
			return new InitializrMetadataV22JsonMapper();
		}
	}

}
