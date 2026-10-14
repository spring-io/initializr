/*
 * Copyright 2012 - present the original author or authors.
 */

package io.spring.initializr.web;

import io.spring.initializr.web.AbstractInitializrControllerIntegrationTests.RestTemplateConfig;
import io.spring.initializr.web.test.MockMvcClientHttpRequestFactory;
import io.spring.initializr.web.test.MockMvcClientHttpRequestFactoryTestExecutionListener;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

/**
 * @author Stephane Nicoll
 */
@ContextConfiguration(classes = RestTemplateConfig.class)
@TestExecutionListeners(mergeMode = MergeMode.MERGE_WITH_DEFAULTS,
		listeners = MockMvcClientHttpRequestFactoryTestExecutionListener.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/snippets", uriPort = 80, uriHost = "start.example.com")
public abstract class AbstractInitializrControllerIntegrationTests extends AbstractInitializrIntegrationTests {

	protected String host = "start.example.com";

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
			return (template) -> template.setRequestFactory(beanFactory.getBean(MockMvcClientHttpRequestFactory.class));
		}

	}

}
