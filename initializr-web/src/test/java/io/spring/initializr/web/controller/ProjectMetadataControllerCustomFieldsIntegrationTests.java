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

import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link ProjectMetadataController} on a real http server.
 *
 * @author Joar Varpe
 */
@ActiveProfiles("test-custom-fields")
class ProjectMetadataControllerCustomFieldsIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

	@Test
	void validateJson() throws JSONException {
		ResponseEntity<String> response = execute("/metadata/config", String.class, null, "application/json");
		validateContentType(response, MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject(response.getBody());
		JSONObject expected = readJsonFrom("metadata/config/test-custom-fields.json");
		JSONAssert.assertEquals(expected, json, JSONCompareMode.STRICT);
	}
}
