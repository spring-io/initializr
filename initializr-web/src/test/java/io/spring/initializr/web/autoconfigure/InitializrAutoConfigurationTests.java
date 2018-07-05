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

package io.spring.initializr.web.autoconfigure;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequestResolver;
import io.spring.initializr.generator.ProjectResourceLocator;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.TemplateRenderer;
import io.spring.initializr.web.project.MainController;
import io.spring.initializr.web.ui.UiController;
import org.junit.Test;
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
public class InitializrAutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class,
					JacksonAutoConfiguration.class, InitializrAutoConfiguration.class));

	@Test
	public void autoConfigRegistersProjectGenerator() {
		this.contextRunner.run(
				(context) -> assertThat(context).hasSingleBean(ProjectGenerator.class));
	}

	@Test
	public void autoConfigWhenProjectGeneratorBeanPresentDoesNotRegisterProjectGenerator() {
		this.contextRunner
				.withUserConfiguration(CustomProjectGeneratorConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(ProjectGenerator.class);
					assertThat(context).hasBean("testProjectGenerator");
				});
	}

	@Test
	public void autoConfigRegistersTemplateRenderer() {
		this.contextRunner.run(
				(context) -> assertThat(context).hasSingleBean(TemplateRenderer.class));
	}

	@Test
	public void autoConfigWhenTemplateRendererBeanPresentDoesNotRegisterTemplateRenderer() {
		this.contextRunner
				.withUserConfiguration(CustomTemplateRendererConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(TemplateRenderer.class);
					assertThat(context).hasBean("testTemplateRenderer");
				});
	}

	@Test
	public void autoConfigRegistersProjectRequestResolver() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(ProjectRequestResolver.class));
	}

	@Test
	public void autoConfigWhenProjectRequestResolverBeanPresentDoesNotRegisterProjectRequestResolver() {
		this.contextRunner
				.withUserConfiguration(CustomProjectRequestResolverConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(ProjectRequestResolver.class);
					assertThat(context).hasBean("testProjectRequestResolver");
				});
	}

	@Test
	public void autoConfigRegistersProjectResourceLocator() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(ProjectResourceLocator.class));
	}

	@Test
	public void autoConfigWhenProjectResourceLocatorBeanPresentDoesNotRegisterProjectResourceLocator() {
		this.contextRunner
				.withUserConfiguration(CustomProjectResourceLocatorConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(ProjectResourceLocator.class);
					assertThat(context).hasBean("testProjectResourceLocator");
				});
	}

	@Test
	public void autoConfigRegistersInitializrMetadataProvider() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(InitializrMetadataProvider.class));
	}

	@Test
	public void autoConfigWhenInitializrMetadataProviderBeanPresentDoesNotRegisterInitializrMetadataProvider() {
		this.contextRunner
				.withUserConfiguration(
						CustomInitializrMetadataProviderConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(InitializrMetadataProvider.class);
					assertThat(context).hasBean("testInitializrMetadataProvider");
				});
	}

	@Test
	public void autoConfigRegistersDependencyMetadataProvider() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(DependencyMetadataProvider.class));
	}

	@Test
	public void autoConfigWhenDependencyMetadataProviderBeanPresentDoesNotRegisterDependencyMetadataProvider() {
		this.contextRunner
				.withUserConfiguration(
						CustomDependencyMetadataProviderConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(DependencyMetadataProvider.class);
					assertThat(context).hasBean("testDependencyMetadataProvider");
				});
	}

	@Test
	public void customRestTemplateBuilderIsUsed() {
		this.contextRunner.withUserConfiguration(CustomRestTemplateConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(InitializrMetadataProvider.class);
					RestTemplate restTemplate = (RestTemplate) new DirectFieldAccessor(
							context.getBean(InitializrMetadataProvider.class))
									.getPropertyValue("restTemplate");
					assertThat(restTemplate.getErrorHandler())
							.isSameAs(CustomRestTemplateConfiguration.errorHandler);
				});
	}

	@Test
	public void webConfiguration() {
		WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
				.withConfiguration(
						AutoConfigurations.of(RestTemplateAutoConfiguration.class,
								JacksonAutoConfiguration.class,
								HttpMessageConvertersAutoConfiguration.class,
								WebMvcAutoConfiguration.class,
								InitializrAutoConfiguration.class));
		webContextRunner.run((context) -> {
			assertThat(context).hasSingleBean(InitializrWebConfig.class);
			assertThat(context).hasSingleBean(MainController.class);
			assertThat(context).hasSingleBean(UiController.class);
		});
	}

	@Test
	public void webConfigurationConditionalOnWebApplication() {
		this.contextRunner.run((context) -> {
			assertThat(context).doesNotHaveBean(InitializrWebConfig.class);
			assertThat(context).doesNotHaveBean(MainController.class);
			assertThat(context).doesNotHaveBean(UiController.class);
		});
	}

	@Test
	public void cacheConfiguration() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(JCacheManagerCustomizer.class));
	}

	@Test
	public void cacheConfigurationConditionalOnClass() {
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
	static class CustomProjectGeneratorConfiguration {

		@Bean
		public ProjectGenerator testProjectGenerator() {
			return Mockito.mock(ProjectGenerator.class);
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
	static class CustomProjectRequestResolverConfiguration {

		@Bean
		public ProjectRequestResolver testProjectRequestResolver() {
			return Mockito.mock(ProjectRequestResolver.class);
		}

	}

	@Configuration
	static class CustomProjectResourceLocatorConfiguration {

		@Bean
		public ProjectResourceLocator testProjectResourceLocator() {
			return Mockito.mock(ProjectResourceLocator.class);
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
