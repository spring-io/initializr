/*
 * Copyright 2012-2019 the original author or authors.
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

package io.spring.initializr.web.autoconfigure;

import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.MainController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;
import io.spring.initializr.web.support.DefaultInitializrMetadataUpdateStrategy;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link InitializrAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
class InitializrAutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class,
					JacksonAutoConfiguration.class, InitializrAutoConfiguration.class));

	@Test
	void autoConfigRegistersTemplateRenderer() {
		this.contextRunner.run(
				(context) -> assertThat(context).hasSingleBean(TemplateRenderer.class));
	}

	@Test
	void autoConfigWhenTemplateRendererBeanPresentDoesNotRegisterTemplateRenderer() {
		this.contextRunner
				.withUserConfiguration(CustomTemplateRendererConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(TemplateRenderer.class);
					assertThat(context).hasBean("testTemplateRenderer");
				});
	}

	@Test
	void autoConfigRegistersInitializrMetadataUpdateStrategy() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(InitializrMetadataUpdateStrategy.class));
	}

	@Test
	void autoConfigWhenInitializrMetadataUpdateStrategyPresentDoesNotRegisterInitializrMetadataUpdateStrategy() {
		this.contextRunner
				.withUserConfiguration(
						CustomInitializrMetadataUpdateStrategyConfiguration.class)
				.run((context) -> {
					assertThat(context)
							.hasSingleBean(InitializrMetadataUpdateStrategy.class);
					assertThat(context).hasBean("testInitializrMetadataUpdateStrategy");
				});
	}

	@Test
	void autoConfigRegistersInitializrMetadataProvider() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(InitializrMetadataProvider.class));
	}

	@Test
	void autoConfigWhenInitializrMetadataProviderBeanPresentDoesNotRegisterInitializrMetadataProvider() {
		this.contextRunner
				.withUserConfiguration(
						CustomInitializrMetadataProviderConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(InitializrMetadataProvider.class);
					assertThat(context).hasBean("testInitializrMetadataProvider");
				});
	}

	@Test
	void autoConfigRegistersDependencyMetadataProvider() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(DependencyMetadataProvider.class));
	}

	@Test
	void autoConfigWhenDependencyMetadataProviderBeanPresentDoesNotRegisterDependencyMetadataProvider() {
		this.contextRunner
				.withUserConfiguration(
						CustomDependencyMetadataProviderConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(DependencyMetadataProvider.class);
					assertThat(context).hasBean("testDependencyMetadataProvider");
				});
	}

	@Test
	void customRestTemplateBuilderIsUsed() {
		this.contextRunner.withUserConfiguration(CustomRestTemplateConfiguration.class)
				.run((context) -> {
					assertThat(context)
							.hasSingleBean(DefaultInitializrMetadataUpdateStrategy.class);
					RestTemplate restTemplate = (RestTemplate) new DirectFieldAccessor(
							context.getBean(
									DefaultInitializrMetadataUpdateStrategy.class))
											.getPropertyValue("restTemplate");
					assertThat(restTemplate.getErrorHandler())
							.isSameAs(CustomRestTemplateConfiguration.errorHandler);
				});
	}

	@Test
	void webConfiguration() {
		WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
				.withConfiguration(
						AutoConfigurations.of(RestTemplateAutoConfiguration.class,
								JacksonAutoConfiguration.class,
								HttpMessageConvertersAutoConfiguration.class,
								WebMvcAutoConfiguration.class,
								InitializrAutoConfiguration.class));
		webContextRunner.run((context) -> {
			assertThat(context).hasSingleBean(InitializrWebConfig.class);
			assertThat(context).hasSingleBean(ProjectGenerationInvoker.class);
			assertThat(context).hasSingleBean(ProjectRequestToDescriptionConverter.class);
			assertThat(context).hasSingleBean(MainController.class);
		});
	}

	@Test
	void webConfigurationConditionalOnWebApplication() {
		this.contextRunner.run((context) -> {
			assertThat(context).doesNotHaveBean(InitializrWebConfig.class);
			assertThat(context).doesNotHaveBean(MainController.class);
		});
	}

	@Test
	void cacheConfiguration() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(JCacheManagerCustomizer.class));
	}

	@Test
	void cacheConfigurationConditionalOnClass() {
		this.contextRunner
				.withClassLoader(new FilteredClassLoader("javax.cache.CacheManager"))
				.run((context) -> assertThat(context)
						.doesNotHaveBean(JCacheManagerCustomizer.class));
	}

	@Configuration
	static class CustomRestTemplateConfiguration {

		private static final ResponseErrorHandler errorHandler = mock(
				ResponseErrorHandler.class);

		@Bean
		public RestTemplateCustomizer testRestTemplateCustomizer() {
			return (b) -> b.setErrorHandler(errorHandler);
		}

	}

	@Configuration
	static class CustomTemplateRendererConfiguration {

		@Bean
		public TemplateRenderer testTemplateRenderer() {
			return Mockito.mock(TemplateRenderer.class);
		}

	}

	@Configuration
	static class CustomInitializrMetadataUpdateStrategyConfiguration {

		@Bean
		public InitializrMetadataUpdateStrategy testInitializrMetadataUpdateStrategy() {
			return Mockito.mock(InitializrMetadataUpdateStrategy.class);
		}

	}

	@Configuration
	static class CustomInitializrMetadataProviderConfiguration {

		@Bean
		public InitializrMetadataProvider testInitializrMetadataProvider() {
			return Mockito.mock(InitializrMetadataProvider.class);
		}

	}

	@Configuration
	static class CustomDependencyMetadataProviderConfiguration {

		@Bean
		public DependencyMetadataProvider testDependencyMetadataProvider() {
			return Mockito.mock(DependencyMetadataProvider.class);
		}

	}

}
