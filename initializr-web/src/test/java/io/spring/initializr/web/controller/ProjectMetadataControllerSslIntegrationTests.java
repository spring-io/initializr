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

package io.spring.initializr.web.controller;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ProjectMetadataController} with {@code forceSsl} enabled.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles({ "test-default", "test-ssl" })
class ProjectMetadataControllerSslIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void forceSslInMetadata() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2.1+json");
		validateMetadata(response, InitializrMetadataVersion.V2_1.getMediaType(), "2.1.0-ssl", JSONCompareMode.STRICT);
	}

	@Test
	void forceSslInMetadataV2() {
		ResponseEntity<String> response = invokeHome(null, "application/vnd.initializr.v2+json");
		validateMetadata(response, InitializrMetadataVersion.V2.getMediaType(), "2.0.0-ssl", JSONCompareMode.STRICT);
	}

}
