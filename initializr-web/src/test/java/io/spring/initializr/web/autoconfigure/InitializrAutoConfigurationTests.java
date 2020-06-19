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
import io.spring.initializr.web.controller.CommandLineMetadataController;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.controller.ProjectMetadataController;
import io.spring.initializr.web.controller.SpringCliDistributionController;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link InitializrAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Madhura Bhave
 */
class InitializrAutoConfigurationTests {

	private static final AutoConfigurations BASIC_AUTO_CONFIGURATIONS = AutoConfigurations
			.of(RestTemplateAutoConfiguration.class, JacksonAutoConfiguration.class, InitializrAutoConfiguration.class);

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(BASIC_AUTO_CONFIGURATIONS);

	@Test
	void autoConfigRegistersTemplateRenderer() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(TemplateRenderer.class));
	}

	@Test
	void autoConfigWhenTemplateRendererBeanPresentDoesNotRegisterTemplateRenderer() {
		this.contextRunner.withUserConfiguration(CustomTemplateRendererConfiguration.class).run((context) -> {
			assertThat(context).hasSingleBean(TemplateRenderer.class);
			assertThat(context).hasBean("testTemplateRenderer");
		});
	}

	@Test
	void metadataProviderWithNoMetadataUpdateStrategyRegistersDefault() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(InitializrMetadataProvider.class));
	}

	@Test
	void metadataProviderWithCustomInitializrMetadataUpdateStrategyIsRegistered() {
		this.contextRunner.withUserConfiguration(CustomInitializrMetadataUpdateStrategyConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(DefaultInitializrMetadataProvider.class);
					assertThat(context.getBean(DefaultInitializrMetadataProvider.class)).hasFieldOrPropertyWithValue(
							"initializrMetadataUpdateStrategy",
							context.getBean("testInitializrMetadataUpdateStrategy"));
				});
	}

	@Test
	void metadataProviderWithCustomInitializrMetadataProvider() {
		this.contextRunner.withUserConfiguration(CustomInitializrMetadataProviderConfiguration.class).run((context) -> {
			assertThat(context).hasSingleBean(InitializrMetadataProvider.class);
			assertThat(context).hasBean("testInitializrMetadataProvider");
		});
	}

	@Test
	void autoConfigRegistersDependencyMetadataProvider() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(DependencyMetadataProvider.class));
	}

	@Test
	void autoConfigWhenDependencyMetadataProviderBeanPresentDoesNotRegisterDependencyMetadataProvider() {
		this.contextRunner.withUserConfiguration(CustomDependencyMetadataProviderConfiguration.class).run((context) -> {
			assertThat(context).hasSingleBean(DependencyMetadataProvider.class);
			assertThat(context).hasBean("testDependencyMetadataProvider");
		});
	}

	@Test
	void webConfiguration() {
		WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
				.withConfiguration(BASIC_AUTO_CONFIGURATIONS);
		webContextRunner.run((context) -> {
			assertThat(context).hasSingleBean(InitializrWebConfig.class);
			assertThat(context).hasSingleBean(ProjectGenerationController.class);
			assertThat(context).hasSingleBean(ProjectMetadataController.class);
			assertThat(context).hasSingleBean(CommandLineMetadataController.class);
			assertThat(context).hasSingleBean(SpringCliDistributionController.class);
		});
	}

	@Test
	void autoConfigWithCustomProjectGenerationController() {
		new WebApplicationContextRunner().withConfiguration(BASIC_AUTO_CONFIGURATIONS)
				.withUserConfiguration(CustomProjectGenerationController.class).run((context) -> {
					assertThat(context).hasSingleBean(ProjectGenerationController.class);
					assertThat(context.getBean(ProjectGenerationController.class))
							.isSameAs(context.getBean("testProjectGenerationController"));
				});

	}

	@Test
	void webConfigurationConditionalOnWebApplication() {
		this.contextRunner.run((context) -> {
			assertThat(context).doesNotHaveBean(InitializrWebConfig.class);
			assertThat(context).doesNotHaveBean(ProjectGenerationController.class);
			assertThat(context).doesNotHaveBean(ProjectMetadataController.class);
			assertThat(context).doesNotHaveBean(CommandLineMetadataController.class);
			assertThat(context).doesNotHaveBean(SpringCliDistributionController.class);
		});
	}

	@Test
	void cacheConfiguration() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(JCacheManagerCustomizer.class));
	}

	@Test
	void cacheConfigurationConditionalOnClass() {
		this.contextRunner.withClassLoader(new FilteredClassLoader("javax.cache.CacheManager"))
				.run((context) -> assertThat(context).doesNotHaveBean(JCacheManagerCustomizer.class));
	}

	@Configuration
	static class CustomTemplateRendererConfiguration {

		@Bean
		TemplateRenderer testTemplateRenderer() {
			return mock(TemplateRenderer.class);
		}

	}

	@Configuration
	static class CustomInitializrMetadataUpdateStrategyConfiguration {

		@Bean
		InitializrMetadataUpdateStrategy testInitializrMetadataUpdateStrategy() {
			return mock(InitializrMetadataUpdateStrategy.class);
		}

	}

	@Configuration
	static class CustomInitializrMetadataProviderConfiguration {

		@Bean
		InitializrMetadataProvider testInitializrMetadataProvider() {
			return mock(InitializrMetadataProvider.class);
		}

	}

	@Configuration
	static class CustomDependencyMetadataProviderConfiguration {

		@Bean
		DependencyMetadataProvider testDependencyMetadataProvider() {
			return mock(DependencyMetadataProvider.class);
		}

	}

	@Configuration
	static class CustomProjectGenerationController {

		@Bean
		ProjectGenerationController<?> testProjectGenerationController() {
			return mock(ProjectGenerationController.class);
		}

	}

}
