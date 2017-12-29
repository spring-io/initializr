/*
 * Copyright 2012-2017 the original author or authors.
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

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
public class MainControllerServiceMetadataIntegrationTests
		extends AbstractFullStackInitializrIntegrationTests {

	@Autowired
	private InitializrMetadataProvider metadataProvider;

	@Test
	public void initializeRemoteConfig() throws Exception {
		InitializrMetadata localMetadata = metadataProvider.get();
		InitializrMetadata metadata = InitializrMetadataBuilder.create()
				.withInitializrMetadata(new UrlResource(createUrl("/metadata/config")))
				.build();
		// Basic assertions
		assertEquals(localMetadata.getDependencies().getContent().size(),
				metadata.getDependencies().getContent().size());
		assertEquals(localMetadata.getTypes().getContent().size(),
				metadata.getTypes().getContent().size());
		assertEquals(localMetadata.getBootVersions().getContent().size(),
				metadata.getBootVersions().getContent().size());
		assertEquals(localMetadata.getPackagings().getContent().size(),
				metadata.getPackagings().getContent().size());
		assertEquals(localMetadata.getJavaVersions().getContent().size(),
				metadata.getJavaVersions().getContent().size());
		assertEquals(localMetadata.getLanguages().getContent().size(),
				metadata.getLanguages().getContent().size());
	}

	@Test
	public void textPlainNotAccepted() {
		try {
			execute("/metadata/config", String.class, null, "text/plain");
		}
		catch (HttpClientErrorException ex) {
			assertEquals(HttpStatus.NOT_ACCEPTABLE, ex.getStatusCode());
		}
	}

	@Test
	public void validateJson() throws JSONException {
		ResponseEntity<String> response = execute("/metadata/config", String.class, null,
				"application/json");
		validateContentType(response, MediaType.APPLICATION_JSON);
		JSONObject json = new JSONObject(response.getBody());
		JSONObject expected = readJsonFrom("metadata/config/test-default.json");
		JSONAssert.assertEquals(expected, json, JSONCompareMode.STRICT);
	}

	@Test
	public void metadataClientRedirect() {
		ResponseEntity<String> response = execute("/metadata/client", String.class, null,
				"application/json");
		validateCurrentMetadata(response);
	}

}
