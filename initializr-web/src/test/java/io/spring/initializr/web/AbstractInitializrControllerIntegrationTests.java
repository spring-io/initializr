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

package io.spring.initializr.web;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests.RestTemplateConfig;
import io.spring.initializr.web.test.MockMvcClientHttpRequestFactory;
import io.spring.initializr.web.test.MockMvcClientHttpRequestFactoryTestExecutionListener;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

/**
 * @author Stephane Nicoll
 */
@ContextConfiguration(classes = RestTemplateConfig.class)
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS, listeners = MockMvcClientHttpRequestFactoryTestExecutionListener.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets", uriPort = 80, uriHost = "start.spring.io")
public abstract class AbstractInitializrControllerIntegrationTests
		extends AbstractInitializrIntegrationTests {

	protected String host = "start.spring.io";

	@Autowired
	private MockMvcClientHttpRequestFactory requests;

	@Override
	protected String createUrl(String context) {
		return (context.startsWith("/") ? context : "/" + context);
	}

	public MockMvcClientHttpRequestFactory getRequests() {
		return this.requests;
	}

	@Configuration
	static class RestTemplateConfig {

		@Bean
		RestTemplateCustomizer mockMvcCustomizer(BeanFactory beanFactory) {
			return (template) -> template.setRequestFactory(
					beanFactory.getBean(MockMvcClientHttpRequestFactory.class));
		}

	}

}
