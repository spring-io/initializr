/*
 * Copyright 2012 - present the original author or authors.
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

package io.spring.initializr.actuate.autoconfigure;

import java.net.URI;

import io.spring.initializr.actuate.stat.ProjectGenerationStatPublisher;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.autoconfigure.InitializrAutoConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.boot.restclient.autoconfigure.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link InitializrStatsAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
class InitializrStatsAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
		.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, InitializrAutoConfiguration.class,
				RestTemplateAutoConfiguration.class, InitializrStatsAutoConfiguration.class));

	@Test
	void autoConfigRegistersProjectGenerationStatPublisher() {
		this.contextRunner.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> assertThat(context).hasSingleBean(ProjectGenerationStatPublisher.class));
	}

	@Test
	void autoConfigRegistersRetryTemplate() {
		this.contextRunner.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> assertThat(context).hasSingleBean(RetryTemplate.class));
	}

	@Test
	void statsRetryTemplateConditionalOnMissingBean() {
		this.contextRunner.withUserConfiguration(CustomStatsRetryTemplateConfiguration.class)
			.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> {
				assertThat(context).hasSingleBean(RetryTemplate.class);
				RetryTemplate retryTemplate = context.getBean(RetryTemplate.class);
				RetryPolicy retryPolicy = retryTemplate.getRetryPolicy();
				BackOff backOff = retryPolicy.getBackOff();
				assertThat(backOff).isInstanceOf(ExponentialBackOff.class);
				ExponentialBackOff exponentialBackOff = (ExponentialBackOff) backOff;
				assertThat(exponentialBackOff.getMultiplier()).isEqualTo(10);
			});
	}

	@Test
	void customRestTemplateBuilderIsUsed() {
		this.contextRunner.withUserConfiguration(CustomRestTemplateConfiguration.class)
			.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
			.run((context) -> {
				assertThat(context).hasSingleBean(ProjectGenerationStatPublisher.class);
				RestTemplate restTemplate = (RestTemplate) new DirectFieldAccessor(
						context.getBean(ProjectGenerationStatPublisher.class))
					.getPropertyValue("restTemplate");
				assertThat(restTemplate).isNotNull();
				assertThat(restTemplate.getErrorHandler()).isSameAs(CustomRestTemplateConfiguration.errorHandler);
			});
	}

	@Test
	void shouldBackOffIfElasticUriIsNotSet() {
		this.contextRunner.run((context) -> assertThat(context).doesNotHaveBean(ProjectGenerationStatPublisher.class));
	}

	@Test
	void shouldBackOffIfElasticUriIsEmpty() {
		this.contextRunner.withPropertyValues("initializr.stats.elastic.uri=")
			.run((context) -> assertThat(context).doesNotHaveBean(ProjectGenerationStatPublisher.class));
	}

	@Configuration
	static class CustomStatsRetryTemplateConfiguration {

		@Bean
		RetryTemplate statsRetryTemplate() {
			RetryTemplate retryTemplate = new RetryTemplate();
			retryTemplate.setRetryPolicy(RetryPolicy.builder()
				.backOff(new ExponentialBackOff(ExponentialBackOff.DEFAULT_INITIAL_INTERVAL, 10))
				.build());
			return retryTemplate;
		}

	}

	@Configuration
	static class InfrastructureConfiguration {

		@Bean
		InitializrMetadataProvider initializrMetadataProvider() {
			return mock(InitializrMetadataProvider.class);
		}

	}

	@Configuration
	@Import(InfrastructureConfiguration.class)
	static class CustomRestTemplateConfiguration {

		private static final ResponseErrorHandler errorHandler = new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) {
				return false;
			}

			@Override
			public void handleError(URI url, HttpMethod method, ClientHttpResponse response) {
			}
		};

		@Bean
		RestTemplateCustomizer testRestTemplateCustomizer() {
			return (b) -> b.setErrorHandler(errorHandler);
		}

	}

}
