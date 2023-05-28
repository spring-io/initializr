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
package io.spring.initializr.actuate.stat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import io.spring.initializr.actuate.stat.MainControllerStatsIntegrationTests.StatsMockController;
import io.spring.initializr.web.AbstractFullStackInitializrIntegrationTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration tests for stats processing.
 *
 * @author Stephane Nicoll
 */
@Import(StatsMockController.class)
@ActiveProfiles({ "test-default", "test-custom-stats" })
class MainControllerStatsIntegrationTests extends AbstractFullStackInitializrIntegrationTests {

    @Autowired
    private StatsMockController statsMockController;

    @Autowired
    private ProjectGenerationStatPublisher projectGenerationStatPublisher;

    @BeforeEach
    void setup() {
        this.statsMockController.stats.clear();
        // Make sure our mock is going to be invoked with the stats
        this.projectGenerationStatPublisher.updateRequestUrl(URI.create("http://localhost:" + this.port + "/elastic/test/my-entity"));
    }

    @Test
    void simpleProject() {
        downloadArchive("/starter.zip?groupId=com.foo&artifactId=bar&dependencies=web");
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        JsonNode json = parseJson(content.json);
        assertThat(json.get("groupId").textValue()).isEqualTo("com.foo");
        assertThat(json.get("artifactId").textValue()).isEqualTo("bar");
        JsonNode list = json.get("dependencies").get("values");
        assertThat(list).hasSize(1);
        assertThat(list.get(0).textValue()).isEqualTo("web");
    }

    @Test
    void authorizationHeaderIsSet() {
        downloadArchive("/starter.zip");
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        String authorization = content.authorization;
        assertThat(authorization).as("Authorization header must be set").isNotNull();
        assertThat(authorization).startsWith("Basic ");
        String token = authorization.substring("Basic ".length(), authorization.length());
        String[] data = new String(Base64Utils.decodeFromString(token)).split(":");
        assertThat(data[0]).as("Wrong user from " + token).isEqualTo("test-user");
        assertThat(data[1]).as("Wrong password " + token).isEqualTo("test-password");
    }

    @Test
    void requestIpNotSetByDefault() {
        downloadArchive("/starter.zip?groupId=com.foo&artifactId=bar&dependencies=web");
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        JsonNode json = parseJson(content.json);
        assertThat(json.has("requestIp")).as("requestIp property should not be set").isFalse();
    }

    @Test
    void requestIpIsSetWhenHeaderIsPresent() throws Exception {
        RequestEntity<?> request = RequestEntity.get(new URI(createUrl("/starter.zip"))).header("X-FORWARDED-FOR", "10.0.0.123").build();
        getRestTemplate().exchange(request, String.class);
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        JsonNode json = parseJson(content.json);
        assertThat(json.get("client").get("ip").textValue()).as("Wrong requestIp").isEqualTo("10.0.0.123");
    }

    @Test
    void requestIpv4IsNotSetWhenHeaderHasGarbage() throws Exception {
        RequestEntity<?> request = RequestEntity.get(new URI(createUrl("/starter.zip"))).header("x-forwarded-for", "foo-bar").build();
        getRestTemplate().exchange(request, String.class);
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        JsonNode json = parseJson(content.json);
        assertThat(json.has("requestIpv4")).as("requestIpv4 property should not be set if value is not a valid IPv4").isFalse();
    }

    @Test
    void requestCountryIsNotSetWhenHeaderIsSetToXX() throws Exception {
        RequestEntity<?> request = RequestEntity.get(new URI(createUrl("/starter.zip"))).header("cf-ipcountry", "XX").build();
        getRestTemplate().exchange(request, String.class);
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        JsonNode json = parseJson(content.json);
        assertThat(json.has("requestCountry")).as("requestCountry property should not be set if value is set to xx").isFalse();
    }

    @Test
    void invalidProjectSillHasStats() {
        assertThatExceptionOfType(HttpClientErrorException.class).isThrownBy(() -> downloadArchive("/starter.zip?type=invalid-type")).satisfies((ex) -> assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        assertThat(this.statsMockController.stats).as("No stat got generated").hasSize(1);
        StatsMockController.Content content = this.statsMockController.stats.get(0);
        JsonNode json = parseJson(content.json);
        assertThat(json.get("groupId").textValue()).isEqualTo("com.example");
        assertThat(json.get("artifactId").textValue()).isEqualTo("demo");
        assertThat(json.has("errorState")).isTrue();
        JsonNode errorState = json.get("errorState");
        assertThat(errorState.get("invalid").booleanValue()).isEqualTo(true);
        assertThat(errorState.get("type").booleanValue()).isEqualTo(true);
        assertThat(errorState.get("message")).isNotNull();
        assertThat(errorState.get("message").textValue()).contains("invalid-type");
    }

    @Test
    void errorPublishingStatsDoesNotBubbleUp() {
        this.projectGenerationStatPublisher.updateRequestUrl(URI.create("http://localhost:" + this.port + "/elastic-error"));
        downloadArchive("/starter.zip");
        assertThat(this.statsMockController.stats).as("No stat should be available").isEmpty();
    }

    @RestController
    protected static class StatsMockController {

        private final List<Content> stats = new ArrayList<>();

        @PostMapping("/elastic/test/my-entity")
        public void handleProjectRequestDocument(RequestEntity<String> input) {
            String authorization = input.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            Content content = new Content(authorization, input.getBody());
            this.stats.add(content);
        }

        @PostMapping("/elastic-error/test/my-entity")
        public void handleExpectedError() {
            throw new IllegalStateException("Expected exception");
        }

        public static class Content {

            private final String authorization;

            private final String json;

            Content(String authorization, String body) {
                this.authorization = authorization;
                this.json = body;
            }
        }
    }
}
