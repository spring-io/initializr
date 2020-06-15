/*
 * Copyright 2012-2020 the original author or authors.
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

import java.nio.file.Files;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.web.controller.CommandLineMetadataController;
import io.spring.initializr.web.controller.DefaultProjectGenerationController;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.controller.ProjectMetadataController;
import io.spring.initializr.web.controller.SpringCliDistributionController;
import io.spring.initializr.web.project.DefaultProjectRequestPlatformVersionTransformer;
import io.spring.initializr.web.project.DefaultProjectRequestToDescriptionConverter;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.ProjectRequestPlatformVersionTransformer;
import io.spring.initializr.web.support.DefaultDependencyMetadataProvider;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;
import io.spring.initializr.web.support.DefaultInitializrMetadataUpdateStrategy;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;

import org.springframework.beans.factory.ObjectProvider;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to configure Spring initializr. In a web environment, configures
 * the necessary controller to serve the applications from the root context.
 *
 * @author Stephane Nicoll
 */
@Configuration
@EnableConfigurationProperties(InitializrProperties.class)
@AutoConfigureAfter({ JacksonAutoConfiguration.class, RestTemplateAutoConfiguration.class })
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
	@ConditionalOnMissingBean(TemplateRenderer.class)
	public MustacheTemplateRenderer templateRenderer(Environment environment,
			ObjectProvider<CacheManager> cacheManager) {
		return new MustacheTemplateRenderer("classpath:/templates",
				determineCache(environment, cacheManager.getIfAvailable()));
	}

	private Cache determineCache(Environment environment, CacheManager cacheManager) {
		if (cacheManager != null) {
			Binder binder = Binder.get(environment);
			boolean cache = binder.bind("spring.mustache.cache", Boolean.class).orElse(true);
			if (cache) {
				return cacheManager.getCache("initializr.templates");
			}
		}
		return new NoOpCache("templates");
	}

	@Bean
	@ConditionalOnMissingBean
	public InitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy(RestTemplateBuilder restTemplateBuilder,
			ObjectMapper objectMapper) {
		return new DefaultInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean(InitializrMetadataProvider.class)
	public InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties,
			InitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy) {
		InitializrMetadata metadata = InitializrMetadataBuilder.fromInitializrProperties(properties).build();
		return new DefaultInitializrMetadataProvider(metadata, initializrMetadataUpdateStrategy);
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
		InitializrWebConfig initializrWebConfig() {
			return new InitializrWebConfig();
		}

		@Bean
		@ConditionalOnMissingBean
		ProjectGenerationController<ProjectRequest> projectGenerationController(
				InitializrMetadataProvider metadataProvider,
				ObjectProvider<ProjectRequestPlatformVersionTransformer> platformVersionTransformer,
				ApplicationContext applicationContext) {
			ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker = new ProjectGenerationInvoker<>(
					applicationContext, new DefaultProjectRequestToDescriptionConverter(platformVersionTransformer
							.getIfAvailable(DefaultProjectRequestPlatformVersionTransformer::new)));
			return new DefaultProjectGenerationController(metadataProvider, projectGenerationInvoker);
		}

		@Bean
		@ConditionalOnMissingBean
		ProjectMetadataController projectMetadataController(InitializrMetadataProvider metadataProvider,
				DependencyMetadataProvider dependencyMetadataProvider) {
			return new ProjectMetadataController(metadataProvider, dependencyMetadataProvider);
		}

		@Bean
		@ConditionalOnMissingBean
		CommandLineMetadataController commandLineMetadataController(InitializrMetadataProvider metadataProvider,
				TemplateRenderer templateRenderer) {
			return new CommandLineMetadataController(metadataProvider, templateRenderer);
		}

		@Bean
		@ConditionalOnMissingBean
		SpringCliDistributionController cliDistributionController(InitializrMetadataProvider metadataProvider) {
			return new SpringCliDistributionController(metadataProvider);
		}

		@Bean
		InitializrModule InitializrJacksonModule() {
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
		JCacheManagerCustomizer initializrCacheManagerCustomizer() {
			return (cacheManager) -> {
				cacheManager.createCache("initializr.metadata",
						config().setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES)));
				cacheManager.createCache("initializr.dependency-metadata", config());
				cacheManager.createCache("initializr.project-resources", config());
				cacheManager.createCache("initializr.templates", config());
			};
		}

		private MutableConfiguration<Object, Object> config() {
			return new MutableConfiguration<>().setStoreByValue(false).setManagementEnabled(true)
					.setStatisticsEnabled(true);
		}

	}

}
