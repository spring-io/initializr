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

package io.spring.initializr.web.mapper;

import org.springframework.http.MediaType;

/**
 * Define the supported metadata version.
 *
 * @author Stephane Nicoll
 */
public enum InitializrMetadataVersion {

	/**
	 * HAL-compliant metadata.
	 */
	V2("application/vnd.initializr.v2+json"),

	/**
	 * Add "versionRange" attribute to any dependency to specify which Spring Boot
	 * versions are compatible with it. Also provide a separate "dependencies" endpoint to
	 * query dependencies metadata.
	 */
	V2_1("application/vnd.initializr.v2.1+json");

	private final MediaType mediaType;

	InitializrMetadataVersion(String mediaType) {
		this.mediaType = MediaType.parseMediaType(mediaType);
	}

	public MediaType getMediaType() {
		return this.mediaType;
	}

}
