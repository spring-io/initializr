/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.web.project;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class MainControllerDependenciesTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	public void noBootVersion() throws JSONException {
		ResponseEntity<String> response = execute("/dependencies", String.class, null,
				"application/json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE);
		validateDependenciesOutput("1.1.4", response.getBody());
	}

	@Test
	public void filteredDependencies() throws JSONException {
		ResponseEntity<String> response = execute(
				"/dependencies?bootVersion=1.2.1.RELEASE", String.class, null,
				"application/json");
		assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
		validateContentType(response, CURRENT_METADATA_MEDIA_TYPE);
		validateDependenciesOutput("1.2.1", response.getBody());
	}

	protected void validateDependenciesOutput(String version, String actual)
			throws JSONException {
		JSONObject expected = readJsonFrom(
				"metadata/dependencies/test-dependencies-" + version + ".json");
		JSONAssert.assertEquals(expected, new JSONObject(actual), JSONCompareMode.STRICT);
	}

}
