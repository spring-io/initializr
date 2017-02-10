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

import java.util.Collections;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests;
import io.spring.initializr.web.project.LegacyStsControllerIntegrationTests.LegacyConfig;
import org.junit.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import static org.junit.Assert.assertTrue;

/**
 * @author Stephane Nicoll
 */
@ActiveProfiles("test-default")
@ContextConfiguration(classes = LegacyConfig.class)
public class LegacyStsControllerIntegrationTests
		extends AbstractInitializrControllerIntegrationTests {

	@Test
	public void legacyStsHome() {
		String body = htmlHome();
		assertTrue("groupId not found", body.contains("com.example"));
		assertTrue("artifactId not found", body.contains("demo"));
		assertTrue("custom description not found",
				body.contains("Demo project for Spring Boot"));
		assertTrue("Wrong body:\n" + body,
				body.contains("<input type=\"radio\" name=\"language\" value=\"groovy\"/>"));
		assertTrue("Wrong body:\n" + body,
				body.contains("<input type=\"radio\" name=\"language\" value=\"java\" checked=\"true\"/>"));
	}

	@Override
	protected String htmlHome() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		return getRestTemplate().exchange(createUrl("/sts"), HttpMethod.GET,
				new HttpEntity<Void>(headers), String.class).getBody();
	}

	@Configuration
	protected static class LegacyConfig {

		@Bean
		public LegacyStsController legacyStsController(
				InitializrMetadataProvider metadataProvider,
				ResourceUrlProvider resourceUrlProvider) {
			return new LegacyStsController(metadataProvider, resourceUrlProvider);
		}

	}

}