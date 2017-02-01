/*
 * Copyright 2012-2017 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import com.github.benmanes.caffeine.cache.Caffeine;

import io.spring.initializr.generator.ProjectGenerator;
import io.spring.initializr.generator.ProjectRequestPostProcessor;
import io.spring.initializr.generator.ProjectRequestResolver;
import io.spring.initializr.generator.ProjectResourceLocator;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.util.GroovyTemplate;
import io.spring.initializr.web.project.MainController;
import io.spring.initializr.web.support.DefaultDependencyMetadataProvider;
import io.spring.initializr.web.support.DefaultInitializrMetadataProvider;
import io.spring.initializr.web.ui.UiController;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to configure Spring initializr. In a web environment,
 * configures the necessary controller to serve the applications from the
 * root context.
 *
 * <p>Project generation can be customized by defining a custom
 * {@link ProjectGenerator}.
 *
 * @author Stephane Nicoll
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(InitializrProperties.class)
public class InitializrAutoConfiguration {

	@Autowired(required = false)
	private List<ProjectRequestPostProcessor> postProcessors = new ArrayList<>();

	@Bean
	public WebConfig webConfig() {
		return new WebConfig();
	}

	@Bean
	@ConditionalOnMissingBean
	public MainController initializrMainController(InitializrMetadataProvider metadataProvider,
											GroovyTemplate groovyTemplate,
											ResourceUrlProvider resourceUrlProvider,
											ProjectGenerator projectGenerator,
											DependencyMetadataProvider dependencyMetadataProvider) {
		return new MainController(metadataProvider, groovyTemplate, resourceUrlProvider
				, projectGenerator, dependencyMetadataProvider);
	}

	@Bean
	@ConditionalOnMissingBean
	public UiController initializrUiController() {
		return new UiController();
	}

	@Bean
	@ConditionalOnMissingBean
	public ProjectGenerator projectGenerator() {
		return new ProjectGenerator();
	}

	@Bean
	@ConditionalOnMissingBean
	public GroovyTemplate groovyTemplate(Environment environment) {
		RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment, "spring.groovy.template.");
		boolean cache = resolver.getProperty("cache", Boolean.class, true);
		GroovyTemplate groovyTemplate = new GroovyTemplate();
		groovyTemplate.setCache(cache);
		return groovyTemplate;
	}

	@Bean
	@ConditionalOnMissingBean
	public ProjectRequestResolver projectRequestResolver() {
		return new ProjectRequestResolver(postProcessors);
	}

	@Bean
	public ProjectResourceLocator projectResourceLocator() {
		return new ProjectResourceLocator();
	}

	@Bean
	@ConditionalOnMissingBean(InitializrMetadataProvider.class)
	public InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties) {
		InitializrMetadata metadata = InitializrMetadataBuilder.fromInitializrProperties(properties).build();
		return new DefaultInitializrMetadataProvider(metadata, new RestTemplate());
	}

	@Bean
	@ConditionalOnMissingBean
	public DependencyMetadataProvider dependencyMetadataProvider() {
		return new DefaultDependencyMetadataProvider();
	}

	@Bean
	@ConditionalOnMissingBean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(
				createConcurrentMapCache(600L, "initializr"),
				new ConcurrentMapCache("dependency-metadata"),
				new ConcurrentMapCache("project-resources")));
		return cacheManager;
	}

	private static Cache createConcurrentMapCache(Long timeToLive, String name) {
		return new CaffeineCache(name, Caffeine
				.newBuilder()
				.expireAfterWrite(timeToLive, TimeUnit.SECONDS)
				.build());
	}

}
