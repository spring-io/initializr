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

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ProjectMetadataController} on a real http server.
 *
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
class ProjectMetadataControllerCustomDefaultsIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

    @Autowired
    private InitializrMetadataProvider metadataProvider;

    @Test
    void initializeRemoteConfig() throws Exception {
        InitializrMetadata localMetadata = this.metadataProvider.get();
        InitializrMetadata metadata = InitializrMetadataBuilder.create().withInitializrMetadata(new UrlResource(createUrl("/metadata/config"))).build();
        // Basic assertions
        assertThat(metadata.getDependencies().getContent()).hasSameSizeAs(localMetadata.getDependencies().getContent());
        assertThat(metadata.getTypes().getContent()).hasSameSizeAs(localMetadata.getTypes().getContent());
        assertThat(metadata.getBootVersions().getContent()).hasSameSizeAs(localMetadata.getBootVersions().getContent());
        assertThat(metadata.getPackagings().getContent()).hasSameSizeAs(localMetadata.getPackagings().getContent());
        assertThat(metadata.getJavaVersions().getContent()).hasSameSizeAs(localMetadata.getJavaVersions().getContent());
        assertThat(metadata.getLanguages().getContent()).hasSameSizeAs(localMetadata.getLanguages().getContent());
    }

    @Test
    void textPlainNotAccepted() {
        try {
            execute("/metadata/config", String.class, null, "text/plain");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Test
    void validateJson() throws JSONException {
        ResponseEntity<String> response = execute("/metadata/config", String.class, null, "application/json");
        validateContentType(response, MediaType.APPLICATION_JSON);
        JSONObject json = new JSONObject(response.getBody());
        JSONObject expected = readJsonFrom("metadata/config/test-default.json");
        JSONAssert.assertEquals(expected, json, JSONCompareMode.STRICT);
    }

    @Test
    void metadataClientEndpoint() {
        ResponseEntity<String> response = execute("/metadata/client", String.class, null, "application/json");
        validateDefaultMetadata(response);
    }

    @Test
    void dependenciesNoAcceptHeaderWithNoBootVersion() throws JSONException {
        validateDependenciesMetadata("*/*", DEFAULT_METADATA_MEDIA_TYPE);
    }

    @Test
    void dependenciesV21WithNoBootVersion() throws JSONException {
        validateDependenciesMetadata("application/vnd.initializr.v2.1+json", DEFAULT_METADATA_MEDIA_TYPE);
    }

    @Test
    void dependenciesV22WithNoBootVersion() throws JSONException {
        validateDependenciesMetadata("application/vnd.initializr.v2.2+json", CURRENT_METADATA_MEDIA_TYPE);
    }

    private void validateDependenciesMetadata(String acceptHeader, MediaType expectedMediaType) throws JSONException {
        ResponseEntity<String> response = execute("/dependencies", String.class, null, acceptHeader);
        assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
        validateContentType(response, expectedMediaType);
        validateDependenciesOutput("2.4.4", response.getBody());
    }

    @Test
    void filteredDependencies() throws JSONException {
        ResponseEntity<String> response = execute("/dependencies?bootVersion=2.6.1", String.class, null, "application/json");
        assertThat(response.getHeaders().getFirst(HttpHeaders.ETAG)).isNotNull();
        validateContentType(response, DEFAULT_METADATA_MEDIA_TYPE);
        validateDependenciesOutput("2.6.1", response.getBody());
    }

    protected void validateDependenciesOutput(String version, String actual) throws JSONException {
        JSONObject expected = readJsonFrom("metadata/dependencies/test-dependencies-" + version + ".json");
        JSONAssert.assertEquals(expected, new JSONObject(actual), JSONCompareMode.STRICT);
    }
}
