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
package io.spring.initializr.web.support;

import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Tests for {@link SpringIoInitializrMetadataUpdateStrategy}.
 *
 * @author Stephane Nicoll
 */
class SpringIoInitializrMetadataUpdateStrategyTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        this.restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
    }

    @Test
    void bootVersionsAreReplaced() {
        InitializrMetadata metadata = new InitializrMetadataTestBuilder().addBootVersion("0.0.9.RELEASE", true).addBootVersion("0.0.8.RELEASE", false).build();
        assertThat(metadata.getBootVersions().getDefault().getId()).isEqualTo("0.0.9.RELEASE");
        SpringIoInitializrMetadataUpdateStrategy provider = new SpringIoInitializrMetadataUpdateStrategy(this.restTemplate, objectMapper);
        expectJson(metadata.getConfiguration().getEnv().getSpringBootMetadataUrl(), "metadata/springio/spring-boot.json");
        InitializrMetadata updatedMetadata = provider.update(metadata);
        assertThat(updatedMetadata.getBootVersions()).isNotNull();
        List<DefaultMetadataElement> updatedBootVersions = updatedMetadata.getBootVersions().getContent();
        assertThat(updatedBootVersions).hasSize(7);
        assertBootVersion(updatedBootVersions.get(0), "3.0.2 (SNAPSHOT)", false);
        assertBootVersion(updatedBootVersions.get(1), "3.0.1", true);
        assertBootVersion(updatedBootVersions.get(2), "2.7.8 (SNAPSHOT)", false);
        assertBootVersion(updatedBootVersions.get(3), "2.7.7", false);
        assertBootVersion(updatedBootVersions.get(4), "2.6.14", false);
        assertBootVersion(updatedBootVersions.get(5), "2.5.14", false);
        assertBootVersion(updatedBootVersions.get(6), "2.4.13", false);
    }

    @Test
    void defaultBootVersionIsAlwaysSet() {
        InitializrMetadata metadata = new InitializrMetadataTestBuilder().addBootVersion("0.0.9.RELEASE", true).addBootVersion("0.0.8.RELEASE", false).build();
        assertThat(metadata.getBootVersions().getDefault().getId()).isEqualTo("0.0.9.RELEASE");
        SpringIoInitializrMetadataUpdateStrategy provider = new SpringIoInitializrMetadataUpdateStrategy(this.restTemplate, objectMapper);
        expectJson(metadata.getConfiguration().getEnv().getSpringBootMetadataUrl(), "metadata/springio/spring-boot-no-default.json");
        InitializrMetadata updatedMetadata = provider.update(metadata);
        assertThat(updatedMetadata.getBootVersions()).isNotNull();
        List<DefaultMetadataElement> updatedBootVersions = updatedMetadata.getBootVersions().getContent();
        assertThat(updatedBootVersions).hasSize(7);
        assertBootVersion(updatedBootVersions.get(0), "3.0.2 (SNAPSHOT)", true);
        assertBootVersion(updatedBootVersions.get(1), "3.0.1", false);
        assertBootVersion(updatedBootVersions.get(2), "2.7.8 (SNAPSHOT)", false);
        assertBootVersion(updatedBootVersions.get(3), "2.7.7", false);
        assertBootVersion(updatedBootVersions.get(4), "2.6.14", false);
        assertBootVersion(updatedBootVersions.get(5), "2.5.14", false);
        assertBootVersion(updatedBootVersions.get(6), "2.4.13", false);
    }

    private static void assertBootVersion(DefaultMetadataElement actual, String name, boolean defaultVersion) {
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.isDefault()).isEqualTo(defaultVersion);
    }

    private void expectJson(String url, String bodyPath) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        this.mockServer.expect(requestTo(url)).andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).body(new ClassPathResource(bodyPath)).headers(httpHeaders));
    }
}
