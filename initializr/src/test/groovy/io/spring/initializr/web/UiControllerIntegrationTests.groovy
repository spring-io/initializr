/*
 * Copyright 2012-2015 the original author or authors.
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

package io.spring.initializr.web

import org.json.JSONObject
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles('test-default')
class UiControllerIntegrationTests extends AbstractInitializrControllerIntegrationTests {

	@Test
	void dependenciesNoVersion() {
		ResponseEntity<String> response = execute('/ui/dependencies', String, null, null)
		validateContentType(response, MediaType.APPLICATION_JSON)
		validateDependenciesOutput('all', new JSONObject(response.body))
	}

	@Test
	void dependenciesSpecificVersion() {
		ResponseEntity<String> response = execute('/ui/dependencies?version=1.1.2.RELEASE', String, null, null)
		validateContentType(response, MediaType.APPLICATION_JSON)
		validateDependenciesOutput('1.1.2', new JSONObject(response.body))
	}

	protected void validateDependenciesOutput(String version, JSONObject actual) {
		def expected = readJsonFrom("metadata/dependencies/test-dependencies-$version" + ".json")
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
	}

}
