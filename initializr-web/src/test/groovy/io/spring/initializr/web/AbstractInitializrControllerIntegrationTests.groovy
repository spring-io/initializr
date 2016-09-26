/*
 * Copyright 2012-2016 the original author or authors.
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

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.web.client.RestTemplateCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc

import static org.junit.Assert.assertTrue
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment

/**
 * @author Stephane Nicoll
 */
@ContextConfiguration(classes = RestTemplateConfig.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir="target/snippets")
abstract class AbstractInitializrControllerIntegrationTests extends AbstractInitializrIntegrationTests {

	String createUrl(String context) {
		context.startsWith('/') ? context : '/' + context
	}

	@Configuration
	static class RestTemplateConfig {
		
		@Bean
		RestTemplateCustomizer mockMvcCustomizer(MockMvc mockMvc) {
			{ template ->
				template.setRequestFactory(new MockMvcClientHttpRequestFactory(mockMvc))
			}
		}
	}
}
