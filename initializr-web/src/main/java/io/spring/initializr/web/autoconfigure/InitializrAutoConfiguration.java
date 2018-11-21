/*
 * Copyright 2012-2019 the original author or authors.
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

import java.nio.file.Files;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.util.TemplateRenderer;
import io.spring.initializr.web.ProjectResourceLocator;
import io.spring.initializr.web.project.MainController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;
import io.spring.initializr.web.support.DefaultDependencyMetadataProvider;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;
import io.spring.initializr.web.ui.UiController;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to configure Spring initializr. In a web environment, configures
 * the necessary controller to serve the applications from the root context.
 *
 * @author Stephane Nicoll
 */
@Configuration
@EnableConfigurationProperties(InitializrProperties.class)
@AutoConfigureAfter({ JacksonAutoConfiguration.class,
		RestTemplateAutoConfiguration.class })
public class InitializrAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ProjectDirectoryFactory projectDirectoryFactory() {
		return (description) -> Files.createTempDirectory("project-");
	}

	@Bean
	@ConditionalOnMissingBean
	public IndentingWriterFactory indentingWriterFactory() {
		return IndentingWriterFactory.create(new SimpleIndentStrategy("\t"));
	}

	@Bean
	@ConditionalOnMissingBean
	public TemplateRenderer templateRenderer(Environment environment) {
		Binder binder = Binder.get(environment);
		boolean cache = binder.bind("spring.mustache.cache", Boolean.class).orElse(true);
		TemplateRenderer templateRenderer = new TemplateRenderer();
		templateRenderer.setCache(cache);
		return templateRenderer;
	}

	@Bean
	@ConditionalOnMissingBean
	public ProjectResourceLocator projectResourceLocator() {
		return new ProjectResourceLocator();
	}

	@Bean
	@ConditionalOnMissingBean(InitializrMetadataProvider.class)
	public InitializrMetadataProvider initializrMetadataProvider(
			InitializrProperties properties, ObjectMapper objectMapper,
			RestTemplateBuilder restTemplateBuilder) {
		InitializrMetadata metadata = InitializrMetadataBuilder
				.fromInitializrProperties(properties).build();
		return new DefaultInitializrMetadataProvider(metadata, objectMapper,
				restTemplateBuilder.build());
	}

	@Bean
	@ConditionalOnMissingBean
	public DependencyMetadataProvider dependencyMetadataProvider() {
		return new DefaultDependencyMetadataProvider();
	}

	/**
	 * Initializr web configuration.
	 */
	@Configuration
	@ConditionalOnWebApplication
	static class InitializrWebConfiguration {

		@Bean
		public InitializrWebConfig initializrWebConfig() {
			return new InitializrWebConfig();
		}

		@Bean
		@ConditionalOnMissingBean
		public MainController initializrMainController(
				InitializrMetadataProvider metadataProvider,
				TemplateRenderer templateRenderer,
				ResourceUrlProvider resourceUrlProvider,
				DependencyMetadataProvider dependencyMetadataProvider,
				ProjectGenerationInvoker projectGenerationInvoker) {
			return new MainController(metadataProvider, templateRenderer,
					resourceUrlProvider, dependencyMetadataProvider,
					projectGenerationInvoker);
		}

		@Bean
		@ConditionalOnMissingBean
		public ProjectGenerationInvoker projectGenerationInvoker(
				ApplicationContext applicationContext,
				ApplicationEventPublisher eventPublisher,
				ProjectRequestToDescriptionConverter projectRequestToDescriptionConverter) {
			return new ProjectGenerationInvoker(applicationContext, eventPublisher,
					projectRequestToDescriptionConverter);
		}

		@Bean
		public ProjectRequestToDescriptionConverter projectRequestToDescriptionConverter() {
			return new ProjectRequestToDescriptionConverter();
		}

		@Bean
		@ConditionalOnMissingBean
		public UiController initializrUiController(
				InitializrMetadataProvider metadataProvider) {
			return new UiController(metadataProvider);
		}

		@Bean
		public InitializrModule InitializrJacksonModule() {
			return new InitializrModule();
		}

	}

	/**
	 * Initializr cache configuration.
	 */
	@Configuration
	@ConditionalOnClass(javax.cache.CacheManager.class)
	static class InitializrCacheConfiguration {

		@Bean
		public JCacheManagerCustomizer initializrCacheManagerCustomizer() {
			return (cacheManager) -> {
				cacheManager.createCache("initializr.metadata",
						config().setExpiryPolicyFactory(
								CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES)));
				cacheManager.createCache("initializr.dependency-metadata", config());
				cacheManager.createCache("initializr.project-resources", config());
				cacheManager.createCache("initializr.templates", config());
			};
		}

		private MutableConfiguration<Object, Object> config() {
			return new MutableConfiguration<>().setStoreByValue(false)
					.setManagementEnabled(true).setStatisticsEnabled(true);
		}

	}

}
