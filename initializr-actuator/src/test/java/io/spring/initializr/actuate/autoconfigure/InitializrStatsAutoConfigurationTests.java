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

package io.spring.initializr.actuate.autoconfigure;

import io.spring.initializr.actuate.stat.ProjectGenerationStatPublisher;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import org.junit.Test;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link InitializrStatsAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class InitializrStatsAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class,
					InitializrStatsAutoConfiguration.class));

	@Test
	public void customRestTemplateBuilderIsUsed() {
		this.contextRunner.withUserConfiguration(CustomRestTemplateConfiguration.class)
				.withPropertyValues("initializr.stats.elastic.uri=http://localhost:9200")
				.run((context) -> {
					assertThat(context)
							.hasSingleBean(ProjectGenerationStatPublisher.class);
					RestTemplate restTemplate = (RestTemplate) new DirectFieldAccessor(
							context.getBean(ProjectGenerationStatPublisher.class))
									.getPropertyValue("restTemplate");
					assertThat(restTemplate.getErrorHandler())
							.isSameAs(CustomRestTemplateConfiguration.errorHandler);
				});
	}

	@Configuration
	static class InfrastructureConfiguration {

		@Bean
		public InitializrMetadataProvider initializrMetadataProvider() {
			return mock(InitializrMetadataProvider.class);
		}

	}

	@Configuration
	@Import(InfrastructureConfiguration.class)
	static class CustomRestTemplateConfiguration {

		private static final ResponseErrorHandler errorHandler = mock(
				ResponseErrorHandler.class);

		@Bean
		public RestTemplateCustomizer testRestTemplateCustomizer() {
			return (b) -> b.setErrorHandler(errorHandler);
		}

	}

}
